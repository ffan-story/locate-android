package org.yry.hellojni;

import android.os.Bundle;
import android.renderscript.Double2;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cn.wanda.locate.swig.BeaconFingerprint;
import cn.wanda.locate.swig.BeaconSignal;
import cn.wanda.locate.swig.EuclidDistanceEstimator;
import cn.wanda.locate.swig.PositionEstimator;


public class MainActivity extends AppCompatActivity {

    // "添加了注释"
    private static final String TAG = MainActivity.class.getSimpleName();

    static {
        System.load("/data/data/org.yry.hellojni/lib/libwanda-locate.so");
    }

    private String TEST_DATA = "-60.208#-65.487#-66.441#-71.957#-73.809#-76.4#-80.396#-81.146#-84.91#-86.418#1.12#2.0#0.0#\n" +
            "-64.265#-65.427#-64.703#-71.841#-73.12#-74.268#-78.533#-81.029#-87.902#-86.975#1.12#2.0#90.0#\n" +
            "-61.112#-68.431#-65.308#-70.342#-75.0#-76.091#-78.121#-80.955#-86.088#-84.267#1.12#2.0#180.0#\n" +
            "-62.684#-73.771#-64.114#-71.297#-69.36#-77.505#-71.743#-80.446#-85.623#-80.4#1.12#2.0#270.0#\n" +
            "-60.322#-61.018#-67.441#-69.106#-73.275#-72.232#-80.875#-80.641#-87.133#-86.011#2.9#2.0#0.0#\n" +
            "-65.918#-61.667#-68.219#-69.328#-74.193#-74.514#-80.479#-81.485#-86.337#-86.011#2.9#2.0#90.0#\n" +
            "-66.784#-62.681#-69.147#-73.823#-75.196#-77.307#-80.204#-79.416#-88.125#-85.865#2.9#2.0#180.0#\n" +
            "-62.228#-61.602#-67.904#-71.281#-75.132#-70.195#-81.178#-77.363#-84.691#-83.447#2.9#2.0#270.0#\n" +
            "-61.867#-70.241#-68.435#-69.652#-77.178#-72.214#-79.301#-78.128#-89.76#-84.174#4.7#2.0#0.0#\n" +
            "-68.298#-67.079#-70.026#-72.167#-73.466#-75.891#-81.723#-76.702#-86.649#-83.66#4.7#2.0#90.0#\n" +
            "-69.812#-61.882#-71.784#-69.099#-74.87#-72.845#-81.549#-75.441#-87.387#-80.5#4.7#2.0#180.0#\n" +
            "-66.735#-65.287#-70.3#-69.179#-75.27#-72.616#-80.173#-74.87#-83.386#-83.093#4.7#2.0#270.0#\n" +
            "-64.351#-59.913#-68.93#-70.193#-77.604#-74.836#-80.275#-78.395#-88.895#-86.115#6.5#2.0#0.0#\n" +
            "-73.553#-65.017#-69.759#-69.982#-76.045#-73.664#-81.108#-78.955#-87.827#-85.6#6.5#2.0#90.0#\n" +
            "-68.812#-64.342#-68.398#-69.404#-76.533#-75.069#-79.461#-81.406#-89.542#-84.535#6.5#2.0#180.0#\n" +
            "-64.4#-64.838#-71.946#-71.277#-73.327#-77.238#-78.286#-79.829#-89.242#-81.5#6.5#2.0#270.0#\n" +
            "-50.798#-68.966#-63.197#-70.321#-73.468#-73.372#-78.776#-76.94#-87.232#-84.949#1.12#3.3#0.0#\n" +
            "-55.443#-70.298#-64.655#-68.269#-76.009#-77.472#-78.523#-78.899#-85.262#-83.621#1.12#3.3#90.0#\n" +
            "-52.89#-66.419#-66.246#-67.966#-71.991#-74.407#-75.547#-79.083#-87.111#-84.626#1.12#3.3#180.0#\n" +
            "-57.357#-71.856#-61.462#-68.339#-71.496#-71.219#-76.25#-76.845#-85.116#-83.632#1.12#3.3#270.0#";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        BeaconFingerprint[] bfs = genBeaconSignals(TEST_DATA, 10);
        double fa[] = {-69.297997, -67.079002, -70.026001, -72.167000, -73.466003, -75.890999, -81.723000, -76.702003, -86.649002, -83.660004};

        double fb[] = {-69.264999, -65.427002, -64.703003, -71.841003, -73.120003, -74.267998, -78.532997, -81.028999, -87.902000, -86.974998};

        double fc[] = {-73.553001, -65.016998, -69.759003, -69.982002, -76.044998, -73.664001, -81.108002, -78.955002, -87.827003, -85.599998};
        EuclidDistanceEstimator euclidDistanceEstimator = new EuclidDistanceEstimator();
        euclidDistanceEstimator.updateFingerprints(bfs);

        double[] faOutput = new double[2];
        euclidDistanceEstimator.estimatePosition(new BeaconSignal(fa), faOutput);

        double[] fbOutput = new double[2];
        euclidDistanceEstimator.estimatePosition(new BeaconSignal(fb), fbOutput);

        double[] fcOutput = new double[2];
        euclidDistanceEstimator.estimatePosition(new BeaconSignal(fc), fcOutput);

        String result = String.format("Estimated Result:\n\nfa: x=%s, y=%s\n\nfb: x=%s, y=%s\n\nfc: x=%s, y=%s",
                faOutput[0], faOutput[1], fbOutput[0], fbOutput[1], fcOutput[0], fcOutput[1]);

        ((TextView) findViewById(R.id.text)).setText(result);
    }

    private BeaconFingerprint[] genBeaconSignals(String inputStr, int attrNum) {
        String[] lines = inputStr.split("\n");
        BeaconFingerprint[] ret = new BeaconFingerprint[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String[] attrs = lines[i].split("#");
            double[] attrValues = new double[attrNum];
            int j = 0;
            for (; j < attrNum; j++) {
                attrValues[j] = Double.valueOf(attrs[j]);
            }
            BeaconSignal bs = new BeaconSignal(attrValues);
            double x = Double.valueOf(attrs[j]);
            double y = Double.valueOf(attrs[j + 1]);
            BeaconFingerprint bf = new BeaconFingerprint(bs, x, y);
            ret[i] = bf;
        }
        return ret;
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
