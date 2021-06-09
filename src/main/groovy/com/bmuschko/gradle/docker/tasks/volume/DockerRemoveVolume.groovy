package com.bmuschko.gradle.docker.tasks.volume

import groovy.transform.CompileStatic
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * https://docs.docker.com/engine/api/v1.41/#operation/VolumeDelete
 */
@CompileStatic
class DockerRemoveVolume extends DockerExistingVolume {

    @Input
    @Optional
    final Property<Boolean> force = project.objects.property(Boolean)

    @Override
    void runRemoteCommand() {
        logger.quiet "Removing volume '${volumeId.get()}'."
        dockerClient.removeVolumeCmd(volumeId.get()).exec()
    }
}
