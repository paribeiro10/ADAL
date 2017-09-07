# adal-connectivity
Android library to verify if the device has an Internet connection and what it's type is!

### Download

Gradle:

```gradle
dependencies {
  compile 'com.massivedisaster.adal:adal-connectivity:0.1.15'
}
```
### Usage

```java
/**
 * Network verification.
 */
private ConnectionChangeReceiver mConnectionReceiver = new ConnectionChangeReceiver() {
    @Override public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        if (ConnectionChangeReceiver.CONNECTIVITY_CHANGE_FILTER.equals(intent.getAction())) {
            checkConnectivity();
        }
    }
};

/**
 * Check whether the device has Internet connectivity or not.
 */
private void checkConnectivity() {
    if (!getActivity().isFinishing() && isVisible()) {
        final boolean isOnline = NetworkUtils.isNetworkConnected(getActivity());
        handleConnectivityStatusChange(isOnline);
    }
}

/**
 * Log the device Internet connectivity status.
 *
 * @param isOnline boolean value indicating whether the device has a connection established or not.
 */
private void handleConnectivityStatusChange(final boolean isOnline) {
    if (isOnline) {
        mTxtMessage.setText(getString(R.string.connectivity_device_online));
        Log.d(getActivity().getClass().getName(), getString(R.string.connectivity_device_online));
    } else {
        mTxtMessage.setText(getString(R.string.connectivity_device_offline));
        Log.d(getActivity().getClass().getName(), getString(R.string.connectivity_device_offline));
    }
}
```

### Contributing
[CONTRIBUTING](../CONTRIBUTING.md)

### License
[MIT LICENSE](../LICENSE.md)
