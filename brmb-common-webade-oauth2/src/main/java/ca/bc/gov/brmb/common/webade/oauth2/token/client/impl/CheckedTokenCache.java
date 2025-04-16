package ca.bc.gov.brmb.common.webade.oauth2.token.client.impl;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.webade.oauth2.token.client.resource.CheckedToken;

public class CheckedTokenCache implements Cache<CheckedToken> {

	private static final Logger logger = LoggerFactory.getLogger(CheckedTokenCache.class);

	private long defaultCacheExpiryMillis;
	private long cachePurgeThreshold;
	
	private ConcurrentMap<String,CacheCheckedToken> cache;
	private SortedSet<CacheCheckedToken> sortedCache;
	
	public CheckedTokenCache() {
		// Default cache expiry value - 10 minutes
		this.defaultCacheExpiryMillis = 1000*60*10;
		// Default cache purge threshold
		this.cachePurgeThreshold = 100;
		this.cache = new ConcurrentHashMap<String,CacheCheckedToken>();
		
		this.sortedCache = new TreeSet<CacheCheckedToken>(new Comparator<CacheCheckedToken>() {

			@Override
			public int compare(CacheCheckedToken o1, CacheCheckedToken o2) {
				int result;
				if(o1.key.equals(o2.key)) {
					result = 0;
				} else if(o1.expiryTimeMillis == o2.expiryTimeMillis) {
					result = o1.key.compareTo(o2.key);
				} else {
					result = Long.valueOf(o1.expiryTimeMillis).compareTo(Long.valueOf(o2.expiryTimeMillis));
				}
				return result;
			}});
	}
	
	@Override
	public CheckedToken get(String key) {
		
		CheckedToken result = null;
		
		long currentTimeMillis = System.currentTimeMillis();
		
		CacheCheckedToken cachedResult = this.cache.get(key);
		
		if(cachedResult!=null) {
			if(cachedResult.expiryTimeMillis>currentTimeMillis) {

				logger.debug("returning cached CheckedToken: "+key);
				result = cachedResult.value;
			} else {
				logger.debug("cache expired: "+key);
				this.cache.remove(key);
			}
		}
		
		return result;
	}
	
	@Override
	public synchronized void put(String key, CheckedToken value) {
		
		long currentTimeMillis = System.currentTimeMillis();
		
		if(this.cache.size()>cachePurgeThreshold) {
			
			for(Iterator<CacheCheckedToken> iter = this.sortedCache.iterator();iter.hasNext();) {
				CacheCheckedToken cachedResult = iter.next();
				if(cachedResult.expiryTimeMillis>currentTimeMillis) {
					break;
				} 

				{
					logger.debug("Purging expired: "+cachedResult.key);
					iter.remove();
					this.cache.remove(cachedResult.key);
				}
			}
		}
		
		CacheCheckedToken cachedResult = new CacheCheckedToken();
		cachedResult.key = key;
		cachedResult.value = value;
		
		long expiryTimeMillis = currentTimeMillis+defaultCacheExpiryMillis;
		if(value.getExp() == null) {
			logger.debug("Caching token using default period: "+(defaultCacheExpiryMillis/1000.0)+" sec");
		} else {
			expiryTimeMillis = value.getExp().longValue() * 1000l;
			logger.debug("Caching token using exp period: "+((expiryTimeMillis-currentTimeMillis)/1000.0)+" sec");
		}
		
		cachedResult.expiryTimeMillis = expiryTimeMillis;
		this.cache.put(key, cachedResult);
		this.sortedCache.add(cachedResult);
	}

	@Override
	public void remove(String key) {
		this.cache.remove(key);
	}
	
	public void setCacheExpiryMillis(long cacheExpiryMillis) {
		this.defaultCacheExpiryMillis = cacheExpiryMillis;
	}

	public void setCachePurgeThreshold(long cachePurgeThreshold) {
		this.cachePurgeThreshold = cachePurgeThreshold;
	}

	private class CacheCheckedToken implements Serializable {

		private static final long serialVersionUID = 1L;
		
		String key;
		CheckedToken value;
		long expiryTimeMillis;
		
		public CacheCheckedToken() {
			// do nothing
		}

		@Override
		public String toString() {
			return "checkedToken:"+key+" expiryTimeMillis:"+expiryTimeMillis;
		}
	}
}
