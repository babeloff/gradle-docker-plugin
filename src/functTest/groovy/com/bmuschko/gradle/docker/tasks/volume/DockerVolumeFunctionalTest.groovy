package com.bmuschko.gradle.docker.tasks.volume

import com.bmuschko.gradle.docker.AbstractGroovyDslFunctionalTest
import org.gradle.testkit.runner.BuildResult

class DockerVolumeFunctionalTest extends AbstractGroovyDslFunctionalTest {

    private static final String IMAGE = 'alpine:3.4'

    def "can create and tear down a volume"() {
        given:
        String uniqueVolumeName = createUniqueVolumeName()
        buildFile << volumeUsage(uniqueVolumeName)
        buildFile << """
            import com.bmuschko.gradle.docker.tasks.volume.DockerInspectVolume

            task inspectNoVolume(type: DockerInspectVolume) {
                targetVolumeId createVolume.getVolumeId()
                dependsOn removeVolume

                onError { error ->
                    println 'inspectNoVolume ' + error
                }
            }

            inspectVolume.finalizedBy removeVolume
        """

        when:
        BuildResult result = build('inspectVolume', 'inspectNoVolume')

        then:
        result.output.contains("inspectVolume $uniqueVolumeName")
        result.output.find(/inspectNoVolume.*volume [a-z0-9]+ not found/)
    }

    def "can create a container and assign a volume and alias"() {
        given:
        String uniqueVolumeName = createUniqueVolumeName()
        buildFile << volumeUsage(uniqueVolumeName)
        buildFile << pullImageTask()
        buildFile << """
            import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
            import com.bmuschko.gradle.docker.tasks.container.DockerInspectContainer

            task createContainer(type: DockerCreateContainer) {
                dependsOn pullImage, inspectVolume
                targetImageId pullImage.getImage()
                hostConfig.volume = createVolume.getVolumeId()
                volumeAliases = ['some-alias']
                cmd = ['/bin/sh']
            }

            task inspectContainer(type: DockerInspectContainer) {
                dependsOn createContainer
                targetContainerId createContainer.getContainerId()
                onNext { container ->
                    println container.volumeSettings.volumes['$uniqueVolumeName'].aliases
                }
            }

            ${containerRemoveTask()}

            inspectContainer.finalizedBy removeContainer, removeVolume
        """

        when:
        BuildResult result = build('inspectContainer')

        then:
        result.output.contains('[some-alias]')
    }

    static String pullImageTask() {
        """
            import com.bmuschko.gradle.docker.tasks.image.DockerPullImage

            task pullImage(type: DockerPullImage) {
                image = '${IMAGE}'
            }
        """
    }

    static String containerRemoveTask() {
        """
            import com.bmuschko.gradle.docker.tasks.container.DockerRemoveContainer

            task removeContainer(type: DockerRemoveContainer) {
                removeVolumes = true
                force = true
                targetContainerId createContainer.getContainerId()
            }
        """
    }

    static String volumeUsage(String volumeName) {
        """
            import com.bmuschko.gradle.docker.tasks.volume.DockerCreateVolume
            import com.bmuschko.gradle.docker.tasks.volume.DockerRemoveVolume
            import com.bmuschko.gradle.docker.tasks.volume.DockerInspectVolume

            task createVolume(type: DockerCreateVolume) {
                volumeName = '$volumeName'
            }

            task removeVolume(type: DockerRemoveVolume) {
                targetVolumeId createVolume.getVolumeId()
            }

            task inspectVolume(type: DockerInspectVolume) {
                dependsOn createVolume
                targetVolumeId createVolume.getVolumeId()

                onNext { volume ->
                    println 'inspectVolume ' + volume.name
                }
            }
        """
    }
}
