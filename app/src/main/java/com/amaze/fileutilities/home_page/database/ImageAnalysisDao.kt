/*
 * Copyright (C) 2021-2022 Team Amaze - Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com>. All Rights reserved.
 *
 * This file is part of Amaze File Utilities.
 *
 * 'Amaze File Utilities' is a registered trademark of Team Amaze. All other product
 * and company names mentioned are trademarks or registered trademarks of their respective owners.
 */

package com.amaze.fileutilities.home_page.database

import androidx.room.*

@Dao
interface ImageAnalysisDao {

    @Query("SELECT * FROM imageanalysis")
    fun getAll(): List<ImageAnalysis>

    @Query("SELECT * FROM imageanalysis WHERE is_sleeping=1")
    fun getAllSleeping(): List<ImageAnalysis>

    @Query("UPDATE imageanalysis SET is_sleeping=0 WHERE file_path IN(:pathList)")
    fun cleanIsSleeping(pathList: List<String>)

    @Query("SELECT * FROM imageanalysis WHERE is_distracted=1")
    fun getAllDistracted(): List<ImageAnalysis>

    @Query("UPDATE imageanalysis SET is_distracted=0 WHERE file_path IN(:pathList)")
    fun cleanIsDistracted(pathList: List<String>)

    @Query("SELECT * FROM imageanalysis WHERE is_sad=1")
    fun getAllSad(): List<ImageAnalysis>

    @Query("UPDATE imageanalysis SET is_sad=0 WHERE file_path IN(:pathList)")
    fun cleanIsSad(pathList: List<String>)

    @Query("SELECT * FROM imageanalysis WHERE face_count=1")
    fun getAllSelfie(): List<ImageAnalysis>

    @Query("UPDATE imageanalysis SET face_count=0 WHERE file_path IN(:pathList)")
    fun cleanIsSelfie(pathList: List<String>)

    @Query("SELECT * FROM imageanalysis WHERE face_count>1")
    fun getAllGroupPic(): List<ImageAnalysis>

    @Query("UPDATE imageanalysis SET face_count=0 WHERE file_path IN(:pathList)")
    fun cleanIsGroupPic(pathList: List<String>)

    @Query("SELECT * FROM imageanalysis WHERE file_path=:path")
    fun findByPath(path: String): ImageAnalysis?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(analysis: ImageAnalysis)

    @Delete
    fun deleteAll(vararg analysis: ImageAnalysis)

    @Delete
    fun delete(user: ImageAnalysis)

    @Query("DELETE FROM imageanalysis WHERE file_path like '%' || :path || '%'")
    fun deleteByPathContains(path: String)
}
