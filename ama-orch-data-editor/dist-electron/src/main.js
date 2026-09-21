import { app, BrowserWindow, dialog, ipcMain } from 'electron';
import { join } from 'node:path';
import { readFile } from 'node:fs/promises';
const isDevelopment = Boolean(process.env.VITE_DEV_SERVER_URL);
function createWindow() {
    const window = new BrowserWindow({
        width: 1440,
        height: 900,
        minWidth: 980,
        minHeight: 650,
        backgroundColor: '#111827',
        webPreferences: {
            preload: join(__dirname, 'preload.js'),
            contextIsolation: true,
            nodeIntegration: false,
        },
    });
    if (isDevelopment) {
        void window.loadURL(process.env.VITE_DEV_SERVER_URL);
        window.webContents.openDevTools({ mode: 'detach' });
    }
    else {
        void window.loadFile(join(__dirname, '../dist/index.html'));
    }
}
ipcMain.handle('open-json', async () => {
    const result = await dialog.showOpenDialog({
        title: 'JSONファイルを選択',
        properties: ['openFile'],
        filters: [{ name: 'JSON files', extensions: ['json'] }],
    });
    if (result.canceled || result.filePaths.length === 0)
        return null;
    const filePath = result.filePaths[0];
    return { name: filePath.split(/[\\/]/).pop() ?? filePath, content: await readFile(filePath, 'utf8') };
});
app.whenReady().then(() => {
    createWindow();
    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0)
            createWindow();
    });
});
app.on('window-all-closed', () => {
    if (process.platform !== 'darwin')
        app.quit();
});
