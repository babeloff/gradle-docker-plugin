package com.bmuschko.gradle.docker.tasks.volume


import com.github.dockerjava.api.command.InspectVolumeCmd
import com.github.dockerjava.api.command.InspectVolumeResponse
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * https://docs.docker.com/engine/api/v1.41/#tag/Volume
 */
@CompileStatic
class DockerInspectVolume extends DockerExistingVolume {

    @Input
    final Property<String> volumeId = project.objects.property(String)

    DockerInspectVolume() {
        defaultResponseHandling()
    }

    @Override
    void runRemoteCommand() {
        logger.quiet "Inspecting volume '${volumeId.get()}'."
        InspectVolumeCmd volumeCmd = dockerClient.inspectVolumeCmd(volumeId.get())
        InspectVolumeResponse volume = volumeCmd.exec()

        if (nextHandler) {
            nextHandler.execute(volume)
        }
    }

    private void defaultResponseHandling() {
        Action<InspectVolumeResponse> action = new Action<InspectVolumeResponse>() {
            @Override
            void execute(InspectVolumeResponse volume) {
                logger.quiet "Name          : $volume.name"
                logger.quiet "Labels        : $volume.labels"
                logger.quiet "MountPoint    : $volume.mountpoint"
                logger.quiet "Driver        : $volume.driver"
            }
        }
    }
}
