export { };

declare global {
    interface Window {
        desktop: {
            openJson: () => Promise<{ name: string; content: string } | null>;
        };
    }
}
