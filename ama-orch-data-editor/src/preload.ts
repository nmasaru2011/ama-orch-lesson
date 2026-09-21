import { contextBridge, ipcRenderer } from 'electron';

contextBridge.exposeInMainWorld('desktop', {
    openJson: () => ipcRenderer.invoke('open-json'),
});
