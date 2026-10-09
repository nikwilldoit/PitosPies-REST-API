package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.User;
import com.pitospies.pitospieswebservice.models.Role;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findUserByUsername(String username);
    User findUserByStatus(String status);
    User findUserByEmail(String email);
    User findUserByCode(String code);


    @Query("SELECT role.name as role " +
            "FROM role " +
            "    JOIN user_role ON role.id=user_role.role_id " +
            "    JOIN user ON user.id = user_role.user_id " +
            "WHERE user.id=:userId")
    List<String> findUserRoles(int userId);

    @Query("SELECT COUNT(*) FROM user WHERE status<>'verified'")
    Integer cntUnverifiedUsers();

    @Modifying
    @Query("INSERT INTO user_role (user_id, role_id) VALUES (:userId, :roleId) ")
    void addRoleToUser(int userId, int roleId);

    @Modifying
    default void saveWithRole(User user, Role role) {
        save(user);
        addRoleToUser(user.getId(), role.getId());
    }

    @Modifying
    @Query("DELETE FROM user WHERE status!='verified'")
    void deleteUnverifiedUsers();

}