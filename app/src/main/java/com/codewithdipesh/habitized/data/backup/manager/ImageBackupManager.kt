package com.codewithdipesh.habitized.data.backup.manager

import android.content.Context
import android.util.Base64
import com.codewithdipesh.habitized.data.backup.model.ImageFileData
import com.codewithdipesh.habitized.data.backup.model.ImageProgressBackup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageBackupManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val IMAGE_DIRECTORY = "habit_images"
    }


    suspend fun encodeAllImages(
        imageProgressList: List<ImageProgressBackup>
    ): List<ImageFileData> = withContext(Dispatchers.IO) {
        val images = mutableListOf<ImageFileData>()
        val processedPaths = mutableSetOf<String>()

        imageProgressList.forEach { progress ->
            val imagePath = progress.imagePath

            // Skip if already processed
            if (processedPaths.contains(imagePath)) {
                return@forEach
            }

            try {
                val file = File(imagePath)
                if (file.exists() && file.isFile) {
                    val bytes = file.readBytes()
                    val base64Content = Base64.encodeToString(bytes, Base64.DEFAULT)

                    images.add(
                        ImageFileData(
                            fileName = file.name,
                            base64Content = base64Content
                        )
                    )
                    processedPaths.add(imagePath)
                }
            } catch (e: Exception) {
                // Log warning but continue with other images
            }
        }

        images
    }


    suspend fun restoreImages(images: List<ImageFileData>) = withContext(Dispatchers.IO) {
        val imagesDir = getImagesDirectory()

        images.forEach { imageData ->
            try {
                val bytes = Base64.decode(imageData.base64Content, Base64.DEFAULT)
                val file = File(imagesDir, imageData.fileName)

                // Create parent directories if needed
                file.parentFile?.mkdirs()

                file.writeBytes(bytes)
            } catch (e: Exception) {
                // Log warning but continue with other images
            }
        }
    }


    suspend fun clearExistingImages() = withContext(Dispatchers.IO) {
        try {
            val imagesDir = getImagesDirectory()
            if (imagesDir.exists()) {
                imagesDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            // Log warning but continue
        }
    }


    private fun getImagesDirectory(): File {
        val dir = File(context.filesDir, IMAGE_DIRECTORY)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }


    fun getNewImagePath(fileName: String): String {
        val imagesDir = getImagesDirectory()
        return File(imagesDir, fileName).absolutePath
    }
}
