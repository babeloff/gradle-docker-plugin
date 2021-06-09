package com.bmuschko.gradle.docker.tasks.volume

import com.bmuschko.gradle.docker.tasks.AbstractDockerRemoteApiTask
import groovy.transform.CompileStatic
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input

import java.util.concurrent.Callable

@CompileStatic
abstract class DockerExistingVolume extends AbstractDockerRemoteApiTask {
    /**
     * The ID or name of the volume to perform the operation on.
     * The volume for the provided ID has to be created first.
     */
    @Input
    final Property<String> volumeId = project.objects.property(String)

    /**
     * Sets the target volume ID or name.
     *
     * @param volumeId Volume ID or name
     * @see #targetVolumeId(Callable)
     * @see #targetVolumeId(Provider)
     */
    void targetVolumeId(String volumeId) {
        this.volumeId.set(volumeId)
    }

    /**
     * Sets the target volume ID or name.
     *
     * @param volumeId Volume ID or name as Callable
     * @see #targetVolumeId(String)
     * @see #targetVolumeId(Provider)
     */
    void targetVolumeId(Callable<String> volumeId) {
        targetVolumeId(project.provider(volumeId))
    }

    /**
     * Sets the target volume ID or name.
     *
     * @param volumeId Volume ID or name as Provider
     * @see #targetVolumeId(String)
     * @see #targetVolumeId(Callable)
     */
    void targetVolumeId(Provider<String> volumeId) {
        this.volumeId.set(volumeId)
    }
}
