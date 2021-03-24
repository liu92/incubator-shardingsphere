/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.readwrite.splitting.common.algorithm;

import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.readwrite.splitting.spi.ReplicaLoadBalanceAlgorithm;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-robin replica load-balance algorithm.
 */
@Getter
@Setter
public final class RoundRobinReplicaLoadBalanceAlgorithm implements ReplicaLoadBalanceAlgorithm {
    
    private static final ConcurrentHashMap<String, AtomicInteger> COUNTS = new ConcurrentHashMap<>();
    
    private Properties props = new Properties();
    
    @Override
    public String getDataSource(final String name, final String writeDataSourceName, final List<String> readDataSourceNames) {
        AtomicInteger count = COUNTS.containsKey(name) ? COUNTS.get(name) : new AtomicInteger(0);
        COUNTS.putIfAbsent(name, count);
        count.compareAndSet(readDataSourceNames.size(), 0);
        return readDataSourceNames.get(Math.abs(count.getAndIncrement()) % readDataSourceNames.size());
    }
    
    @Override
    public String getType() {
        return "ROUND_ROBIN";
    }
}
