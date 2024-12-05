package com.tencent.cloud.polaris.discovery.refresh;

import java.util.List;

import com.tencent.polaris.api.pojo.Instance;

/**
 * Callback for service instance change.
 *
 * @author Haotian Zhang
 */
public interface ServiceInstanceChangeCallback {

	void callback(List<Instance> currentServiceInstances, List<Instance> addServiceInstances, List<Instance> deleteServiceInstances);
}
