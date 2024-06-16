/*
 * Tencent is pleased to support the open source community by making Spring Cloud Tencent available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package org.springframework.tsf.core.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tag implements Serializable {
    // 每次对这个结构的 JSON 序列化结果有改动时，必须修改 VERSION 并写兼容性代码
    public static final int VERSION = 1;

    public enum ControlFlag {
        /**
         * 表示标签要在调用中传递下去，默认不启用
         */
        @SerializedName("0")
        TRANSITIVE,

        /**
         * 表示标签不被使用在服务鉴权，默认是被使用的
         */
        @SerializedName("1")
        NOT_IN_AUTH,

        /**
         * 表示标签不被使用在服务路由，默认是被使用的
         */
        @SerializedName("2")
        NOT_IN_ROUTE,

        /**
         * 表示标签不被使用在调用链，默认是被使用的
         */
        @SerializedName("3")
        NOT_IN_SLEUTH,

        /**
         * 表示标签不被使用在调用链，默认是被使用的
         */
        @SerializedName("4")
        NOT_IN_LANE,
        
        /**
         * 表示标签被使用在单元化场景，默认是不被使用的
         */
        @SerializedName("5")
        IN_UNIT
    }

    public enum Scene {
        /**
         * 不限场景
         */
        NO_SPECIFIC,
        AUTH, ROUTE, SLEUTH, LANE, UNIT
    }

    @SerializedName("k")
    @Expose
    private String key;

    @SerializedName("v")
    @Expose
    private String value;

    @SerializedName("f")
    @Expose
    private Set<ControlFlag> flags = new HashSet<>();

    public Tag(String key, String value, ControlFlag... flags) {
        this.key = key;
        this.value = value;
        this.flags = new HashSet<>(Arrays.asList(flags));
    }

    public Tag() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Set<ControlFlag> getFlags() {
        return flags;
    }

    public void setFlags(Set<ControlFlag> flags) {
        this.flags = flags;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Tag) {
            Tag tag = (Tag) object;
            return (key == null ? tag.key == null : key.equals(tag.key))
                    && (flags == null ? tag.flags == null : flags.equals(tag.flags));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) + (flags == null ? 0 : flags.hashCode());
    }


    @Override
    public String toString() {
        return "Tag{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", flags=" + flags +
                '}';
    }
}
