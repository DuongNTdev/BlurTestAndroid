package at.favre.app.blurbenchmark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import at.favre.app.blurbenchmark.blur.EBlurAlgorithm;
import at.favre.app.blurbenchmark.models.BenchmarkWrapper;
import at.favre.app.blurbenchmark.models.StatInfo;
import at.favre.app.blurbenchmark.util.BenchmarkUtil;
import at.favre.app.blurbenchmark.util.BitmapUtil;
import at.favre.app.blurbenchmark.util.BlurUtil;

/**
 * Created by PatrickF on 14.04.2014.
 */
public class BlurBenchmarkTask extends AsyncTask<Void, Void, BenchmarkWrapper> {
	private static final String TAG = BlurBenchmarkTask.class.getSimpleName();
    private static final int WARMUP_ROUNDS = 3;

	private StatInfo statInfo;

	private long startWholeProcess;

	private int bitmapDrawableResId;

	private Bitmap master;
	private int benchmarkRounds;
	private int radius;
	private EBlurAlgorithm algorithm;
	private Context ctx;
	private RenderScript rs;
	private boolean run=false;

	public BlurBenchmarkTask(int bitmapDrawableResId, int benchmarkRounds, int radius, EBlurAlgorithm algorithm, RenderScript rs, Context ctx) {
		this.bitmapDrawableResId = bitmapDrawableResId;
		this.benchmarkRounds = benchmarkRounds;
		this.radius = radius;
		this.algorithm = algorithm;
		this.rs = rs;
		this.ctx = ctx;
	}


	@Override
	protected void onPreExecute() {
		Log.d(TAG,"Start test with "+radius+"px radius, "+benchmarkRounds+"rounds in "+algorithm);
		startWholeProcess = BenchmarkUtil.elapsedRealTimeNanos();
	}

	@Override
	protected BenchmarkWrapper doInBackground(Void... voids) {
		try {
			run=true;
			long startReadBitmap = BenchmarkUtil.elapsedRealTimeNanos();
			final BitmapFactory.Options options = new BitmapFactory.Options();
			master = BitmapFactory.decodeResource(ctx.getResources(), bitmapDrawableResId, options);
			long readBitmapDuration = (BenchmarkUtil.elapsedRealTimeNanos() - startReadBitmap)/1000000l;

			statInfo = new StatInfo(master.getHeight(), master.getWidth(),radius,algorithm,benchmarkRounds);
			statInfo.setLoadBitmap(readBitmapDuration);

			Bitmap blurredBitmap = null;

            Log.d(TAG,"Warmup");
            for (int i = 0; i < WARMUP_ROUNDS; i++) {
				if(!run) {
					break;
				}
                BenchmarkUtil.elapsedRealTimeNanos();
                blurredBitmap = master.copy(master.getConfig(), true);
                blurredBitmap = BlurUtil.blur(rs,ctx, blurredBitmap, radius, algorithm);
            }

            Log.d(TAG,"Start benchmark");
			for (int i = 0; i < benchmarkRounds; i++) {
				if(!run) {
					break;
				}
				long startBlur = BenchmarkUtil.elapsedRealTimeNanos();
				blurredBitmap = master.copy(master.getConfig(), true);
				blurredBitmap = BlurUtil.blur(rs,ctx, blurredBitmap, radius, algorithm);
				statInfo.getBenchmarkData().add((BenchmarkUtil.elapsedRealTimeNanos() - startBlur)/1000000d);
			}

			if(!run) {
				return null;
			}

			statInfo.setBenchmarkDuration((BenchmarkUtil.elapsedRealTimeNanos() - startWholeProcess)/1000000l);

			String fileName = master.getWidth()+"x"+master.getHeight()+"_" + radius + "px_" + algorithm + ".png";
			return new BenchmarkWrapper(BitmapUtil.saveBitmap(blurredBitmap, fileName, BitmapUtil.getCacheDir(ctx), false),
					BitmapUtil.saveBitmap(BitmapUtil.flip(blurredBitmap),"mirror_"+fileName,BitmapUtil.getCacheDir(ctx),true),
					statInfo);
		} catch (Throwable e) {
            Log.e(TAG,"Could not complete benchmark",e);
			return new BenchmarkWrapper(null,null, new StatInfo(e.toString(),algorithm));
		}
	}

	public void cancelBenchmark() {
		run =false;
		Log.d(TAG,"canceled");
	}

	@Override
	protected void onPostExecute(BenchmarkWrapper bitmap) {
		master.recycle();
		master = null;
		Log.d(TAG,"test done");
	}


}
