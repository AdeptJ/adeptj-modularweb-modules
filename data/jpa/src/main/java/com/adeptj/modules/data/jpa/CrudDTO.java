/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.data.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CrudDTO
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class CrudDTO<T extends BaseEntity> {

    private CrudDTO() {
    }

    private Class<T> entity;

    private String namedQuery;

    private String jpaQuery;

    private List<Object> posParams;

    private int startPos;

    private int maxResult;

    private CrudDTO(Class<T> entity) {
        this.entity = entity;
    }

    public Class<T> getEntity() {
        return entity;
    }

    public String getNamedQuery() {
        return namedQuery;
    }

    public String getJpaQuery() {
        return jpaQuery;
    }

    public List<Object> getPosParams() {
        return posParams;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link CrudDTO}
     */
    public static class Builder {

        // no public access as available through static method.
        private Builder() {
        }

        private Class<? extends BaseEntity> entity;

        private String namedQuery;

        private String jpaQuery;

        private List<Object> posParams;

        private int startPos;

        private int maxResult;

        public <T extends BaseEntity> Builder entity(Class<T> entity) {
            this.entity = entity;
            return this;
        }

        public Builder namedQuery(String namedQuery) {
            this.namedQuery = namedQuery;
            return this;
        }

        public Builder jpaQuery(String jpaQuery) {
            this.jpaQuery = jpaQuery;
            return this;
        }

        public Builder addPosParam(Object param) {
            if (this.posParams == null) {
                this.posParams = new ArrayList<>();
            }
            this.posParams.add(param);
            return this;
        }

        public Builder addPosParams(Object... params) {
            if (this.posParams == null) {
                this.posParams = new ArrayList<>();
            }
            this.posParams.addAll(Arrays.asList(params));
            return this;
        }

        public Builder startPos(int startPos) {
            this.startPos = startPos;
            return this;
        }

        public Builder maxResult(int maxResult) {
            this.maxResult = maxResult;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T extends BaseEntity> CrudDTO<T> build() {
            CrudDTO<T> crudDTO = new CrudDTO<>((Class<T>) this.entity);
            crudDTO.namedQuery = this.namedQuery;
            crudDTO.jpaQuery = this.jpaQuery;
            crudDTO.posParams = this.posParams;
            crudDTO.startPos = this.startPos;
            crudDTO.maxResult = this.maxResult;
            return crudDTO;
        }
    }
}
