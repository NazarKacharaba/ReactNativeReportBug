package com.sample_2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private ReactPackage reactPackage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.list);
        final MyAdapter adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        reactPackage = new DefaultPackage(new Resizable() {
            @Override
            public void onSizeChanged(int newWidth, int newHeight) {
                Log.d(TAG, "onSizeChanged() called with: newWidth = ["
                        + newWidth
                        + "], newHeight = ["
                        + newHeight
                        + "]");
                adapter.setNewSize(newHeight, newWidth);
            }
        });
    }

    class MyAdapter extends ArrayAdapter {

        private int newHeight;
        private int newWidth;

        public MyAdapter(Context context) {
            super(context, R.layout.item);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =
                    LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false);
            ReactRootView mReactRootView = (ReactRootView) view.findViewById(R.id.test_js);
            ReactInstanceManager mReactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(getApplication())
                    .setBundleAssetName("main.jsbundle")
                    .setJSMainModuleName("index.android")
                    .addPackage(new MainReactPackage())
                    .addPackage(reactPackage)
                    .setUseDeveloperSupport(BuildConfig.DEBUG)
                    .setInitialLifecycleState(LifecycleState.RESUMED)
                    .build();
            mReactRootView.startReactApplication(mReactInstanceManager, "Sample_2", null);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = newHeight;
            layoutParams.width = newWidth;
            view.setLayoutParams(layoutParams);
            return view;
        }

        public void setNewSize(int newHeight, int newWidth) {
            this.newHeight = newHeight;
            this.newWidth = newWidth;
            notifyDataSetChanged();
        }
    }
}
