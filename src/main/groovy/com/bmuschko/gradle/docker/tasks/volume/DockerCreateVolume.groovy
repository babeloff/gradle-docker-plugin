package com.bmuschko.gradle.docker.tasks.volume

import com.bmuschko.gradle.docker.tasks.AbstractDockerRemoteApiTask
import com.github.dockerjava.api.command.CreateVolumeResponse
import groovy.transform.CompileStatic
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

/**
 * https://docs.docker.com/engine/api/v1.41/#operation/VolumeCreate
 */
@CompileStatic
class DockerCreateVolume extends AbstractDockerRemoteApiTask {
    /**
     * The name of the volume to be created.
     *
     * @since 7.x
     */
    @Input
    final Property<String> volumeName = project.objects.property(String)

    /**
     *  Default:  "local"
     *  Name of the volume driver to use.
     */
    @Input
    final Property<String> driverName = project.objects.property(String)

    /**
     * A mapping of driver options and values.
     * These options are passed directly to the driver and are driver specific.
     */
    @Input
    @Optional
    final MapProperty<String, String> driverOpts = project.objects.mapProperty(String, String)

    /**
     * User-defined key/value metadata.
     */
    @Input
    @Optional
    final MapProperty<String, String> labels = project.objects.mapProperty(String, String)

    /**
     * The id of the created volume.
     */
    @Internal
    final Property<String> volumeId = project.objects.property(String)

    void runRemoteCommand() {
        logger.quiet "Creating volume '${volumeName.get()}'."
        CreateVolumeResponse volume = dockerClient.createVolumeCmd().withName(volumeName.get()).exec()

        if (nextHandler) {
            nextHandler.execute(volume)
        }

        String createdVolumeId = volume.name
        volumeId.set(createdVolumeId)
        logger.quiet "Created volume with ID '$createdVolumeId'."
    }
}
