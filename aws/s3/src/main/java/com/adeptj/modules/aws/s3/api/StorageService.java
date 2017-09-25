/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
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
package com.adeptj.modules.aws.s3.api;

import com.adeptj.modules.aws.s3.UploadRequest;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Service for various operations on AWS S3.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public interface StorageService {

    Bucket createBucket(String bucketName);

    void deleteBucket(String bucketName);

    PutObjectResult createFolder(String bucketName, String folderName);

    PutObjectResult uploadFile(UploadRequest request);

    S3Object getFile(String bucketName, String key);

    void deleteFile(String bucketName, String key);

    String getSigningRegion();
}
