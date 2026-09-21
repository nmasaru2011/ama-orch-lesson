"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const electron_1 = require("electron");
const node_path_1 = require("node:path");
const promises_1 = require("node:fs/promises");
const isDevelopment = Boolean(process.env.VITE_DEV_SERVER_URL);
function createWindow() {
    const window = new electron_1.BrowserWindow({
        width: 1440,
        height: 900,
        minWidth: 980,
        minHeight: 650,
        backgroundColor: '#111827',
        webPreferences: {
            preload: (0, node_path_1.join)(__dirname, 'preload.js'),
            contextIsolation: true,
            nodeIntegration: false,
        },
    });
    if (isDevelopment) {
        void window.loadURL(process.env.VITE_DEV_SERVER_URL);
        window.webContents.openDevTools({ mode: 'detach' });
    }
    else {
        void window.loadFile((0, node_path_1.join)(__dirname, '../dist/index.html'));
    }
}
electron_1.ipcMain.handle('open-json', async () => {
    const result = await electron_1.dialog.showOpenDialog({
        title: 'JSONファイルを選択',
        properties: ['openFile'],
        filters: [{ name: 'JSON files', extensions: ['json'] }],
    });
    if (result.canceled || result.filePaths.length === 0)
        return null;
    const filePath = result.filePaths[0];
    return { name: filePath.split(/[\\/]/).pop() ?? filePath, content: await (0, promises_1.readFile)(filePath, 'utf8') };
});
electron_1.app.whenReady().then(() => {
    createWindow();
    electron_1.app.on('activate', () => {
        if (electron_1.BrowserWindow.getAllWindows().length === 0)
            createWindow();
    });
});
electron_1.app.on('window-all-closed', () => {
    if (process.platform !== 'darwin')
        electron_1.app.quit();
});
