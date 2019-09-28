package com.n26.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Varadharajan on 2019-09-27 23:53
 * @project name: coding-challenge
 */
public class Statistics {
	private BigDecimal sum = new BigDecimal("0");
	private BigDecimal avg = new BigDecimal("0");
	private BigDecimal max = null;
	private BigDecimal min = null;
	private Long count = 0L;

	public void add(BigDecimal occurrence) {
		if (this.min == null || occurrence.compareTo(this.min) < 0)
			this.min = occurrence;
		if (this.max == null || occurrence.compareTo(this.max) > 0)
			this.max = occurrence;
		this.count++;
		this.sum = this.sum.add(occurrence);
		this.avg = this.sum.divide(new BigDecimal(this.count), 2, RoundingMode.HALF_EVEN);
	}

	public void add(Statistics sample) {
		if (sample.getMin() != null) {
			if (this.getMin() == null || sample.getMin().compareTo(this.getMin()) < 0)
				this.setMin(sample.getMin());
		}

		if (sample.getMax() != null) {
			if (this.getMax() == null || sample.getMax().compareTo(this.getMax()) > 0)
				this.setMax(sample.getMax());
		}

		this.setSum(this.getSum().add(sample.getSum()));
		this.setCount(this.getCount() + sample.getCount());
		if (this.getCount() != 0)
			this.setAvg(this.getSum().divide(new BigDecimal(this.getCount()), 2, RoundingMode.HALF_EVEN));

	}

	public BigDecimal getAvg() {
		return avg;
	}

	public void setAvg(BigDecimal avg) {
		this.avg = avg;
	}

	public BigDecimal getSum() {
		return sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
