package com.example.alpha;

class Upload {
    String nm1, imgUrl;

    public Upload() {

    }

    public Upload(String nm, String url) {
        if (nm.trim().equals("")) {
            nm = "no name";
        }
        nm1 = nm;
        imgUrl = url;
    }

    public String getName() {
        return nm1;
    }

    public void setName(String nm) {
        nm1 = nm;
    }

    public String getImageUrl() {
        return imgUrl;
    }

    public void setImgUrl(String url) {
        imgUrl = url;
    }
}
