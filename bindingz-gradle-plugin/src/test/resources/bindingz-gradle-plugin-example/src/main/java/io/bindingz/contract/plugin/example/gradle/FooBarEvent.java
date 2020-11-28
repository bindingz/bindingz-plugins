/*
 * Copyright (c) 2020 Connor Goulding
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.bindingz.contract.plugin.example.gradle;

import io.bindingz.api.annotations.Contract;

import java.time.LocalDateTime;
import java.util.List;

@Contract(contractName = "FooBarEvent", owner = "bindingz-gradle-plugin-example", version = "1.1-SNAPSHOT")
public class FooBarEvent {
    private String id;
    private String name;
    private List<Bar> bars;
    private LocalDateTime localDateTime;

    public FooBarEvent(String id, String name, List<io.bindingz.contract.plugin.example.gradle.FooBarEvent.Bar> bars, LocalDateTime localDateTime) {
        this.id = id;
        this.name = name;
        this.bars = bars;
        this.localDateTime = localDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<io.bindingz.contract.plugin.example.gradle.FooBarEvent.Bar> getBars() {
        return bars;
    }

    public void setBars(List<io.bindingz.contract.plugin.example.gradle.FooBarEvent.Bar> bars) {
        this.bars = bars;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public static class Bar {
        private final String barId;

        public Bar(String barId) {
            this.barId = barId;
        }

        public String getBarId() {
            return barId;
        }
    }
}
