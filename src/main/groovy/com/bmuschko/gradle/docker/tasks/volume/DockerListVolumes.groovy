package com.bmuschko.gradle.docker.tasks.volume

import com.bmuschko.gradle.docker.tasks.AbstractDockerRemoteApiTask
import com.github.dockerjava.api.command.ListVolumesCmd

/**
 * https://docs.docker.com/engine/api/v1.41/#tag/Volume
 */

import com.github.dockerjava.api.command.ListVolumesResponse
import com.github.dockerjava.api.model.Volume
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * https://docs.docker.com/engine/api/v1.41/#operation/VolumeList
 */
@CompileStatic
class DockerListVolumes extends AbstractDockerRemoteApiTask {

    @Input
    @Optional
    final Property<Boolean> showAll = project.objects.property(Boolean)

    @Input
    @Optional
    final Property<Boolean> dangling = project.objects.property(Boolean)

    @Input
    @Optional
    final Property<String> driver = project.objects.property(String)

    @Input
    @Optional
    final MapProperty<String, String> labels = project.objects.mapProperty(String, String)

    @Input
    @Optional
    final Property<String> volumeName = project.objects.property(String)

    DockerListVolumes() {
        defaultResponseHandling()
    }

    void runRemoteCommand() {
        logger.quiet "List volumes."
        ListVolumesCmd listVolumesCmd = dockerClient.listVolumesCmd()
        ListVolumesResponse volumes = listVolumesCmd.exec()

        if (nextHandler) {
            for (volume in volumes) {
                nextHandler.execute(volume)
            }
        }
    }

    private void defaultResponseHandling() {
        Action<Volume> action = new Action<Volume>() {
            @Override
            void execute(Volume volume) {
                logger.quiet "Path         : $volume.path"
                logger.quiet "-----------------------------------------------"
            }
        }

        onNext(action)
    }
}
