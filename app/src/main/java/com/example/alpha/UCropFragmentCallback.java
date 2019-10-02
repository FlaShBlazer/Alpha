package com.example.alpha;

public interface UCropFragmentCallback {

    /**
     * Return loader status
     *
     * @param showLoader
     */
    void loadingProgress(boolean showLoader);

    /**
     * Return cropping result or error
     *
     * @param result
     */
    void onCropFinish(UCropFragment.UCropResult result);

}
