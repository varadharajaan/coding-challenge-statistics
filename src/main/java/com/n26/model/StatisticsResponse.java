package com.n26.model;

/**
 * @author Varadharajan on 2019-09-27 23:59
 * @project name: coding-challenge
 */
public class StatisticsResponse {

	private String sum;
	private String avg;
	private String max;
	private String min;
	private long count = 0;

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getAvg() {
		return avg;
	}

	public void setAvg(String avg) {
		this.avg = avg;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
