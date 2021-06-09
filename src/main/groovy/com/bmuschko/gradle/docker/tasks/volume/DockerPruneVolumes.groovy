package com.bmuschko.gradle.docker.tasks.volume

import com.github.dockerjava.api.model.PruneResponse
import com.github.dockerjava.api.model.PruneType
import groovy.transform.CompileStatic
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
/**
 * https://docs.docker.com/engine/api/v1.41/#operation/VolumePrune
 */
@CompileStatic
class DockerPruneVolumes extends DockerExistingVolume {

    @Input
    @Optional
    final MapProperty<String, String> filters = project.objects.mapProperty(String, String)

    @Override
    void runRemoteCommand() {
        logger.quiet "Pruning volumes."
        PruneResponse pruneResponse = dockerClient.pruneCmd(PruneType.VOLUMES).exec()
    }
}
