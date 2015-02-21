/*
 * Copyright 2015 C. A. Fitzgerald
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.riotopsys.factotum.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.Factotum;
import net.riotopsys.factotum.api.SimpleCancelRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

import java.util.List;

import javax.inject.Inject;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    Factotum factotum;

    private ICallback<List<LibraryEntry>> callback = new HandlerCallback<List<LibraryEntry>>(new Handler()) {

        @Override
        public void onSuccessInHandler(AbstractRequest abstractRequest, List<LibraryEntry> result) {
            adapter.setList(result);
        }

        @Override
        public void onFailureInHandler(AbstractRequest abstractRequest, Object error) {
            Toast.makeText(MainActivity.this, String.format("error: %S", error.toString()), Toast.LENGTH_LONG).show();
        }

    };

    private ListView listView;
    private AnimeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listview);

        adapter = new AnimeAdapter();
        listView.setAdapter(adapter);

        ((SampleApplication)getApplication()).getObjectGraph().inject(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        factotum.issueCancellation(new SimpleCancelRequest(TAG));
    }

    @Override
    protected void onResume() {
        super.onResume();

        factotum.addRequest(new GetLibraryRequest("riotopsys")
                .setGroup(TAG)
                .setCallback(callback));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
