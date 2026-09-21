const state = {
    rows: [],
    columns: [],
    wrapper: null,
    arrayKey: null,
    fileName: 'ama-orch-data.json'
};

const fileInput = document.getElementById('fileInput');
const tableHead = document.getElementById('tableHead');
const tableBody = document.getElementById('tableBody');
const emptyState = document.getElementById('emptyState');
const rowCount = document.getElementById('rowCount');
const fileName = document.getElementById('fileName');
const message = document.getElementById('message');

function setMessage(text) {
    message.textContent = text;
}

function columnsFor(rows) {
    return [...new Set(rows.flatMap(row => Object.keys(row)))];
}

function loadData(value, name) {
    if (Array.isArray(value)) {
        state.rows = value.map(normalizeRow);
        state.wrapper = null;
        state.arrayKey = null;
    } else if (value && typeof value === 'object') {
        const key = Object.keys(value).find(candidate => Array.isArray(value[candidate]));
        if (!key) throw new Error('配列データを含むJSONではありません。');
        state.rows = value[key].map(normalizeRow);
        state.wrapper = { ...value };
        state.arrayKey = key;
    } else {
        throw new Error('JSONのルートは配列または配列を含むオブジェクトにしてください。');
    }
    state.columns = columnsFor(state.rows);
    state.fileName = name || state.fileName;
    fileName.textContent = state.fileName;
    render();
    setMessage(`${state.rows.length}件のデータを読み込みました。`);
}

function normalizeRow(row) {
    return row && typeof row === 'object' && !Array.isArray(row) ? { ...row } : { value: row };
}

function render() {
    rowCount.textContent = `${state.rows.length}件`;
    emptyState.hidden = state.rows.length > 0;
    tableHead.innerHTML = '';
    tableBody.innerHTML = '';
    if (state.rows.length === 0) return;

    const header = document.createElement('tr');
    state.columns.forEach(column => {
        const cell = document.createElement('th');
        cell.textContent = column;
        header.appendChild(cell);
    });
    const actionHeader = document.createElement('th');
    actionHeader.textContent = '操作';
    header.appendChild(actionHeader);
    tableHead.appendChild(header);

    state.rows.forEach((row, rowIndex) => {
        const tr = document.createElement('tr');
        state.columns.forEach(column => {
            const td = document.createElement('td');
            const input = document.createElement('input');
            input.value = formatValue(row[column]);
            input.addEventListener('change', () => {
                row[column] = parseValue(input.value);
            });
            td.appendChild(input);
            tr.appendChild(td);
        });
        const action = document.createElement('td');
        const remove = document.createElement('button');
        remove.className = 'row-delete';
        remove.type = 'button';
        remove.textContent = '削除';
        remove.addEventListener('click', () => {
            state.rows.splice(rowIndex, 1);
            render();
        });
        action.appendChild(remove);
        tr.appendChild(action);
        tableBody.appendChild(tr);
    });
}

function formatValue(value) {
    return value == null ? '' : typeof value === 'object' ? JSON.stringify(value) : String(value);
}

function parseValue(value) {
    const trimmed = value.trim();
    if (trimmed === '') return '';
    try { return JSON.parse(trimmed); } catch { return value; }
}

function addRow() {
    if (state.columns.length === 0) state.columns = ['id', 'name', 'description'];
    state.rows.push(Object.fromEntries(state.columns.map(column => [column, ''])));
    render();
    setMessage('行を追加しました。');
}

function outputData() {
    if (!state.wrapper) return state.rows;
    return { ...state.wrapper, [state.arrayKey]: state.rows };
}

fileInput.addEventListener('change', async event => {
    const file = event.target.files[0];
    if (!file) return;
    try {
        loadData(JSON.parse(await file.text()), file.name);
    } catch (error) {
        setMessage(`読み込みに失敗しました: ${error.message}`);
    }
    fileInput.value = '';
});

document.getElementById('sampleButton').addEventListener('click', () => {
    loadData([
        { orch_id: 'AMA001', orch_name: 'サンプル管弦楽団', activity: true },
        { orch_id: 'AMA002', orch_name: '練習用アンサンブル', activity: false }
    ], 'sample-ama-orch-data.json');
});

document.getElementById('addButton').addEventListener('click', addRow);
document.getElementById('clearButton').addEventListener('click', () => {
    state.rows = [];
    render();
    setMessage('表示中のデータを消去しました。');
});
document.getElementById('downloadButton').addEventListener('click', () => {
    const blob = new Blob([JSON.stringify(outputData(), null, 2)], { type: 'application/json' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = state.fileName.replace(/\.json$/i, '') + '-edited.json';
    link.click();
    URL.revokeObjectURL(link.href);
    setMessage('JSONを保存しました。');
});
document.getElementById('uploadButton').addEventListener('click', async () => {
    const endpoint = document.getElementById('endpointInput').value.trim();
    if (!endpoint) {
        setMessage('送信先URLを入力してください。');
        return;
    }
    try {
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(outputData())
        });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        setMessage('Webアプリへ送信しました。');
    } catch (error) {
        setMessage(`送信に失敗しました: ${error.message}`);
    }
});

render();
