package com.n26.Service;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Varadharajan on 2019-09-28 00:23
 * @project name: coding-challenge
 */
@Configuration
@ConfigurationProperties("com.n26")
public class SamplingStatisticsServiceConfiguration {
	private Duration samplePeriod;
	private Duration samplingPeriod;

	public Duration getSamplePeriod() {
		return samplePeriod;
	}

	public void setSamplePeriod(Duration samplePeriod) {
		this.samplePeriod = samplePeriod;
	}

	public Duration getSamplingPeriod() {
		return samplingPeriod;
	}

	public void setSamplingPeriod(Duration samplingPeriod) {
		this.samplingPeriod = samplingPeriod;
	}
}
