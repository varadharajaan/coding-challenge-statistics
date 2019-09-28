package com.n26.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.n26.Controller.TransactionController;

/**
 * @author Varadharajan on 2019-09-28 00:22
 * @project name: coding-challenge
 */

import com.n26.Exception.ValueNotFoundException;
import com.n26.model.Statistics;
import com.n26.model.Transaction;

/**
 * check if statistic for input timestamp exists already in map
 *
 * ignore transactions which are more than 1 minute old. Such transactions are
 * already discarded by validation layer
 */
@Service
public class SamplingStatisticsService implements StatisticsService {
	private final Duration samplePeriod;
	private final Duration samplingPeriod;
	private final int sampleSize;

	private Map<Instant, Statistics> samples;
	
	private static final Logger logger = LoggerFactory.getLogger(SamplingStatisticsService.class);

	public SamplingStatisticsService(SamplingStatisticsServiceConfiguration configuration) {
		this.samplePeriod = configuration.getSamplePeriod();
		this.samplingPeriod = configuration.getSamplingPeriod();

		this.sampleSize = ((int) (this.samplingPeriod.toNanos() / this.samplePeriod.toNanos())) + 1;
		this.samples = Collections.emptyMap();
	}

	public void reset() {
		synchronized (this) {
			this.samples = Collections.emptyMap();
		}
	}

	public Statistics get() {
		synchronized (this) {
			return _get();
		}
	}

	private Statistics _get() {
		if (this.samples.size() == 0)
			return new Statistics();

		Instant now = Instant.now();
		checkSamples(now);

		return getSamplesStatistics();
	}

	private Statistics getSamplesStatistics() {
		Statistics aggregate = new Statistics();
		this.samples.forEach((key, sample) -> aggregate.add(sample));
		return aggregate;
	}

	public RegisterResult register(Transaction transaction) {
		synchronized (this) {
			return _register(transaction);
		}
	}

	private RegisterResult _register(Transaction transaction) {
		Instant now = Instant.now();

		logger.debug("create transaction in map kind of storage");
		if (transactionHappensInTheFuture(transaction, now))
			throw new ValueNotFoundException("Transactions can't have timestamps of the future");

		checkSamples(now);

		if (transactionIsOutsideSamplingPeriod(transaction, now))
			return RegisterResult.DISCARDED;

		Instant sampleKey = getSampleKey(transaction.getTimestamp()).orElseThrow(IllegalStateException::new);
		updateStatistics(sampleKey, transaction);

		return RegisterResult.REGISTERED;
	}

	private boolean transactionHappensInTheFuture(Transaction transaction, Instant now) {
		return transaction.getTimestamp().isAfter(now);
	}

	private boolean transactionIsOutsideSamplingPeriod(Transaction transaction, Instant now) {
		return now.minus(this.samplingPeriod).isAfter(transaction.getTimestamp());
	}

	private void checkSamples(Instant now) {
		if (this.samples.size() == 0) {
			initializeSamples(now);
		} else if (outsideCurrentSamplingPeriod(now)) {
			if (allSamplesAreInvalid(now))
				initializeSamples(now);
			else
				renewSamples(now);
		}
	}

	private void initializeSamples(Instant now) {
		this.samples = new LinkedHashMap<>(sampleSize, 0.75f, false);

		logger.debug("checking initalized samples");
		Instant sampleKey = now.minus(samplingPeriod);
		for (int i = 0; i < this.sampleSize; i++) {
			this.samples.put(sampleKey, new Statistics());
			sampleKey = sampleKey.plus(samplePeriod);
		}
	}

	private boolean outsideCurrentSamplingPeriod(Instant now) {
		return getPenultimateSampleKey()
				.map(penultimateSampleKey -> now.minus(this.samplingPeriod).isAfter(penultimateSampleKey))
				.orElse(false);
	}

	private Optional<Instant> getOldestSampleKey() {
		// noinspection LoopStatementThatDoesntLoop
		for (Instant sampleKey : this.samples.keySet()) {
			return Optional.of(sampleKey);
		}
		return Optional.empty();
	}

	private Optional<Instant> getPenultimateSampleKey() {
		Iterator<Instant> keys = this.samples.keySet().iterator();
		if (keys.hasNext()) {
			// Discard last key
			keys.next();
			return Optional.of(keys.next());
		} else {
			return Optional.empty();
		}
	}

	private boolean allSamplesAreInvalid(Instant now) {
		return getNewestSampleKey().map(newestSampleKey -> now.minus(this.samplingPeriod).isAfter(newestSampleKey))
				.orElseThrow(IllegalStateException::new);
	}

	private Optional<Instant> getNewestSampleKey() {
		Instant newestSampleKey = null;

		for (Instant sampleKey : this.samples.keySet())
			newestSampleKey = sampleKey;

		return Optional.ofNullable(newestSampleKey);
	}

	private void renewSamples(Instant now) {
		int removed = 0;

		Iterator<Instant> sampleStarts = this.samples.keySet().iterator();
		while (sampleStarts.hasNext()) {
			Instant sampleStart = sampleStarts.next();
			if (!now.minus(samplingPeriod).isAfter(sampleStart.plus(samplePeriod)))
				break;

			sampleStarts.remove();
			removed++;
		}

		Instant newestSampleStart = getNewestSampleKey().orElseThrow(IllegalStateException::new);
		for (int i = 0; i < removed; i++) {
			newestSampleStart = newestSampleStart.plus(samplePeriod);
			this.samples.put(newestSampleStart, new Statistics());
		}
	}

	private Optional<Instant> getSampleKey(Instant timestamp) {
		return getOldestSampleKey().flatMap(oldestSampleKey -> {
			long delta = Duration.between(oldestSampleKey, timestamp).toNanos();
			int sampleKeyIndex = delta != 0 ? (int) (delta / this.samplePeriod.toNanos()) : 0;

			return getSampleKey(sampleKeyIndex);
		});
	}

	private Optional<Instant> getSampleKey(int index) {
		// Is the index out of range?
		if (index >= this.samples.size())
			return Optional.empty();

		Duration difference = Duration.ZERO;
		for (int i = 0; i < index; i++) {
			difference = difference.plus(this.samplePeriod);
		}

		// Assign the difference to a different variable that is effectively final to
		// use it inside lambda
		Duration finalDifference = difference;

		return this.getOldestSampleKey().map(oldestSampleKey -> oldestSampleKey.plus(finalDifference));
	}

	private void updateStatistics(Instant sampleKey, Transaction transaction) {
		Statistics statistics = Optional.ofNullable(this.samples.get(sampleKey))
				.orElseThrow(IllegalArgumentException::new);
		statistics.add(transaction.getAmount());
	}

	@Override
	public void clear() {
		samples.clear();

	}
}
