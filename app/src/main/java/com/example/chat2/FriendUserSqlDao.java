package com.example.chat2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FriendUserSqlDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertFriend(FriendUserSql friend);

    @Query("select username from Friends LIMIT 10;")
    List<String> selectFirstTenFriends();

    @Query("select username from Friends where UID = :uid;")
    String selectUsernameByUID(String uid);
}
