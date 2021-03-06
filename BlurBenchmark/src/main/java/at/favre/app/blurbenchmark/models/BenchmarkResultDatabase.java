package at.favre.app.blurbenchmark.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import at.favre.app.blurbenchmark.blur.EBlurAlgorithm;

/**
 * Created by PatrickF on 16.04.2014.
 */
public class BenchmarkResultDatabase {
	private List<BenchmarkEntry> entryList = new ArrayList<BenchmarkEntry>();

	public List<BenchmarkEntry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<BenchmarkEntry> entryList) {
		this.entryList = entryList;
	}

	@JsonIgnore
	public BenchmarkEntry getByName(String name) {
		for (BenchmarkEntry benchmarkEntry : entryList) {
			if(benchmarkEntry.getName().equals(name)) {
				return benchmarkEntry;
			}
		}
		return null;
	}

	@JsonIgnore
	public List<BenchmarkEntry> getAllByCategory(String category) {
		List<BenchmarkEntry> list = new ArrayList<BenchmarkEntry>();
		for (BenchmarkEntry benchmarkEntry : entryList) {
			if(benchmarkEntry.getCategory().equals(category)) {
				list.add(benchmarkEntry);
			}
		}
		return list;
	}

	@JsonIgnore
	public List<BenchmarkEntry> getAllByBlurRadius(int radius) {
		List<BenchmarkEntry> list = new ArrayList<BenchmarkEntry>();
		for (BenchmarkEntry benchmarkEntry : entryList) {
			if(benchmarkEntry.getRadius() == radius) {
				list.add(benchmarkEntry);
			}
		}
		return list;
	}

	@JsonIgnore
	public TreeSet<String> getAllImageSizes() {
		TreeSet<String> list = new TreeSet<String>();
		for (BenchmarkEntry benchmarkEntry : entryList) {
			list.add(benchmarkEntry.getImageSizeString());
		}
		return list;
	}

	@JsonIgnore
	public BenchmarkEntry getByImageSizeAndRadiusAndAlgorithm(String imageSize,int radius, EBlurAlgorithm algorithm) {
		List<BenchmarkEntry> list = new ArrayList<BenchmarkEntry>();
		for (BenchmarkEntry benchmarkEntry : entryList) {
			if(benchmarkEntry.getImageSizeString().equals(imageSize) && benchmarkEntry.getRadius() == radius && !benchmarkEntry.getWrapper().isEmpty() && benchmarkEntry.getWrapper().get(0).getStatInfo().getAlgorithm().equals(algorithm)) {
				return (benchmarkEntry);
			}
		}
		return null;
	}


	@JsonIgnore
	public Set<Integer> getAllBlurRadii() {
		TreeSet<Integer> list = new TreeSet<Integer>();
		for (BenchmarkEntry benchmarkEntry : entryList) {
			list.add(benchmarkEntry.getRadius());
		}
		return list;
	}

	@JsonIgnore
	public BenchmarkEntry getByCategoryAndAlgorithm(String category, EBlurAlgorithm algorithm) {
		for (BenchmarkEntry benchmarkEntry : entryList) {
			if(benchmarkEntry.getCategory().equals(category)) {
				if(!benchmarkEntry.getWrapper().isEmpty() && benchmarkEntry.getWrapper().get(0).getStatInfo().getAlgorithm().equals(algorithm)) {
					return benchmarkEntry;
				}
			}
		}
		return null;
	}

	@JsonIgnore
	public static BenchmarkWrapper getRecentWrapper(BenchmarkEntry entry) {
		if(entry != null && !entry.getWrapper().isEmpty()) {
			Collections.sort(entry.getWrapper());
			return entry.getWrapper().get(0);
		} else {
			return null;
		}
	}

	public static class BenchmarkEntry {
		private String name;
		private String category;
		private int radius;
		private int height;
		private int width;
		private List<BenchmarkWrapper> wrapper = new ArrayList<BenchmarkWrapper>();

		public BenchmarkEntry() {
		}

		public BenchmarkEntry(String name, String category,int radius,int height, int width,  List<BenchmarkWrapper> wrapper) {
			this.name = name;
			this.category = category;
			this.wrapper = wrapper;
			this.radius = radius;
			this.height = height;
			this.width = width;
		}

		public BenchmarkEntry(BenchmarkWrapper benchmarkWrapper) {
			this(benchmarkWrapper.getStatInfo().getKeyString(),benchmarkWrapper.getStatInfo().getCategoryString(),benchmarkWrapper.getStatInfo().getBlurRadius(),benchmarkWrapper.getStatInfo().getBitmapHeight(),benchmarkWrapper.getStatInfo().getBitmapWidth(),new ArrayList<BenchmarkWrapper>());
		}


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<BenchmarkWrapper> getWrapper() {
			return wrapper;
		}

		public void setWrapper(List<BenchmarkWrapper> wrapper) {
			this.wrapper = wrapper;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public int getRadius() {
			return radius;
		}

		public void setRadius(int radius) {
			this.radius = radius;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		@JsonIgnore
		public String getImageSizeString() {
			return height+"x"+width;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			BenchmarkEntry that = (BenchmarkEntry) o;

			if (name != null ? !name.equals(that.name) : that.name != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return name != null ? name.hashCode() : 0;
		}
	}
}
