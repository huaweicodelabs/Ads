import prompt from '@system.prompt';

export default {
    onShow() {
        const bannerView = this.$child("bannerView");
        if (bannerView) {
            bannerView.load();
        }
    },
    adLoadedHandler() {
        prompt.showToast({
            message: 'Ad loaded',
            duration: 2000,
        });
    },
    adFailedHandler(event) {
        let errorMsg = event && event.detail || "";
        prompt.showToast({
            message: 'Ad error:' + errorMsg,
            duration: 2000,
        });
    },
    adCloseHandler() {
        prompt.showToast({
            message: 'Ad closed',
            duration: 2000,
        });
    }
}
