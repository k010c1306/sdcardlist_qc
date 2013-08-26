package jp.example.fileviewer_qc;

final class MyConstants {
	// クライアント ID: 000000004C0FB400
    static final String APP_CLIENT_ID = "000000004C0FB400";

    public static final String[] SCOPES = {
        "wl.signin",
        "wl.basic",
        "wl.offline_access",
        "wl.skydrive_update",
        "wl.contacts_create",
        "wl.contacts_photos",
        "wl.contacts_skydrive",
    };

    private MyConstants() {
        throw new AssertionError("Unable to create Config object.");
    }
}