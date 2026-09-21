import * as THREE from 'three';
import initSqlJs from 'sql.js';
import './style.css';

type JsonValue = Record<string, unknown> | unknown[];

const app = document.querySelector<HTMLDivElement>('#app')!;
app.innerHTML = `
  <header class="topbar">
    <div class="brand"><span class="brand-mark">AO</span><div><strong>ama-orch</strong><small>DATA EDITOR</small></div></div>
    <div class="toolbar"><span id="file-status" class="file-status">ファイル未読込</span><button id="open-json" class="primary">JSONを開く</button><button id="reset-view" class="ghost">表示をリセット</button></div>
  </header>
  <main class="workspace">
    <aside class="sidebar">
      <section><p class="eyebrow">PROJECT</p><h1>3D データ編集</h1><p class="muted">ローカルファイルを読み込み、空間上で確認します。</p></section>
      <section class="panel"><div class="panel-title">シーン情報 <span id="object-count">0 objects</span></div><div id="scene-list" class="scene-list"><div class="empty">JSONを開くと<br>オブジェクトが表示されます</div></div></section>
      <section class="panel connection"><div class="panel-title">ローカルデータベース</div><div class="db-status"><span class="status-dot"></span><span id="db-status">SQLite 準備中...</span></div><p class="muted small">オフラインで利用可能なSQLite領域を初期化します。</p></section>
    </aside>
    <section class="viewport-wrap"><div id="viewport"></div><div class="viewport-hint">ドラッグ: 回転　ホイール: ズーム</div><div id="toast" class="toast"></div></section>
    <aside class="inspector"><p class="eyebrow">INSPECTOR</p><h2 id="selected-name">オブジェクト未選択</h2><div id="inspector-content" class="inspector-content"><div class="empty">シーン内のオブジェクトを<br>クリックしてください</div></div><div class="legend"><div><span class="legend-swatch coral"></span>読み込みデータ</div><div><span class="legend-swatch blue"></span>選択中</div></div></aside>
  </main>
`;

const viewport = document.querySelector<HTMLDivElement>('#viewport')!;
const scene = new THREE.Scene();
scene.background = new THREE.Color('#101827');
const camera = new THREE.PerspectiveCamera(45, 1, 0.1, 1000);
camera.position.set(7, 5.5, 8);
const renderer = new THREE.WebGLRenderer({ antialias: true });
renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
viewport.appendChild(renderer.domElement);
scene.add(new THREE.HemisphereLight('#dbeafe', '#172033', 2));
const keyLight = new THREE.DirectionalLight('#ffffff', 3);
keyLight.position.set(4, 8, 5);
scene.add(keyLight);
const grid = new THREE.GridHelper(16, 16, '#334155', '#1f2937');
scene.add(grid);
const axes = new THREE.AxesHelper(2.5);
scene.add(axes);
const objects = new Map<THREE.Mesh, string>();
let selected: THREE.Mesh | null = null;
let parsedData: JsonValue | null = null;

function resize() {
    const { width, height } = viewport.getBoundingClientRect();
    camera.aspect = width / height;
    camera.updateProjectionMatrix();
    renderer.setSize(width, height, false);
}
new ResizeObserver(resize).observe(viewport);
resize();

function addObject(name: string, position: [number, number, number], color = '#f07c69') {
    const mesh = new THREE.Mesh(new THREE.BoxGeometry(1.15, 1.15, 1.15), new THREE.MeshStandardMaterial({ color, roughness: 0.52 }));
    mesh.position.set(...position);
    mesh.userData.baseColor = color;
    scene.add(mesh);
    objects.set(mesh, name);
}

function extractItems(data: JsonValue): Array<{ name: string; position: [number, number, number] }> {
    const source = Array.isArray(data) ? data : Object.entries(data).map(([name, value]) => ({ name, value }));
    return source.slice(0, 80).map((item, index) => {
        const value = typeof item === 'object' && item !== null && 'value' in item ? item.value : item;
        const name = typeof item === 'object' && item !== null && 'name' in item ? String(item.name) : `object-${index + 1}`;
        const record = typeof value === 'object' && value !== null && !Array.isArray(value) ? value as Record<string, unknown> : {};
        const x = Number(record.x ?? record.posX ?? (index % 6) * 1.7 - 4.25);
        const y = Number(record.y ?? record.posY ?? 0.6);
        const z = Number(record.z ?? record.posZ ?? Math.floor(index / 6) * 1.7 - 3.4);
        return { name, position: [Number.isFinite(x) ? x : 0, Number.isFinite(y) ? y : 0.6, Number.isFinite(z) ? z : 0] };
    });
}

function renderList() {
    const list = document.querySelector<HTMLDivElement>('#scene-list')!;
    list.innerHTML = '';
    [...objects.entries()].forEach(([mesh, name]) => {
        const item = document.createElement('button');
        item.className = 'scene-item';
        item.innerHTML = `<span class="object-icon"></span><span>${name}</span>`;
        item.onclick = () => selectObject(mesh);
        list.appendChild(item);
    });
    document.querySelector('#object-count')!.textContent = `${objects.size} objects`;
}

function selectObject(mesh: THREE.Mesh) {
    if (selected) (selected.material as THREE.MeshStandardMaterial).color.set(selected.userData.baseColor);
    selected = mesh;
    (mesh.material as THREE.MeshStandardMaterial).color.set('#62b5d8');
    document.querySelector('#selected-name')!.textContent = objects.get(mesh) ?? 'オブジェクト';
    document.querySelector('#inspector-content')!.innerHTML = `<div class="property"><span>位置 X</span><strong>${mesh.position.x.toFixed(2)}</strong></div><div class="property"><span>位置 Y</span><strong>${mesh.position.y.toFixed(2)}</strong></div><div class="property"><span>位置 Z</span><strong>${mesh.position.z.toFixed(2)}</strong></div><div class="property"><span>タイプ</span><strong>Box</strong></div>`;
}

function showToast(message: string) {
    const toast = document.querySelector<HTMLDivElement>('#toast')!;
    toast.textContent = message;
    toast.classList.add('visible');
    window.setTimeout(() => toast.classList.remove('visible'), 2400);
}

document.querySelector('#open-json')!.addEventListener('click', async () => {
    const file = await window.desktop.openJson();
    if (!file) return;
    try {
        parsedData = JSON.parse(file.content) as JsonValue;
        objects.forEach((_name, mesh) => scene.remove(mesh));
        objects.clear();
        extractItems(parsedData).forEach((item) => addObject(item.name, item.position));
        renderList();
        document.querySelector('#file-status')!.textContent = file.name;
        showToast(`${objects.size}件のデータを読み込みました`);
    } catch {
        showToast('JSONの形式を確認してください');
    }
});

document.querySelector('#reset-view')!.addEventListener('click', () => {
    camera.position.set(7, 5.5, 8);
    camera.lookAt(0, 0, 0);
});

const raycaster = new THREE.Raycaster();
const pointer = new THREE.Vector2();
renderer.domElement.addEventListener('click', (event) => {
    const rect = renderer.domElement.getBoundingClientRect();
    pointer.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
    pointer.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
    raycaster.setFromCamera(pointer, camera);
    const hit = raycaster.intersectObjects([...objects.keys()])[0];
    if (hit) selectObject(hit.object as THREE.Mesh);
});

void initSqlJs({ locateFile: (file) => new URL(`/node_modules/sql.js/dist/${file}`, window.location.origin).href }).then((SQL) => {
    const database = new SQL.Database();
    database.run('CREATE TABLE IF NOT EXISTS imported_files (name TEXT, content TEXT, imported_at TEXT)');
    document.querySelector('#db-status')!.textContent = 'SQLite オフライン接続済み';
    database.close();
});

function animate() {
    requestAnimationFrame(animate);
    renderer.render(scene, camera);
}
animate();
