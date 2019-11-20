package com.tl.mybatis_plus;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tl.mybatis_plus.entity.User;
import com.tl.mybatis_plus.mapper.UserMapper;
import org.apache.ibatis.annotations.Update;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tanglei
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class QueryWrapperTests {

    @Autowired
    private UserMapper userMapper;
    @Test
    public void testDelete() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .isNull("name")
                .ge("age", 12)
                .isNotNull("email");
        int result = userMapper.delete(queryWrapper);
        System.out.println("delete return count = " + result);
        //UPDATE user SET deleted=1 WHERE deleted=0 AND name IS NULL AND age >= ? AND email IS NOT NULL
    }


    @Test
    public void testSelectOne() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", "Tom");

        User user = userMapper.selectOne(queryWrapper);//只能返回一条记录，多余一条则抛出异常
        System.out.println(user);
        //SELECT id,name,age,email,create_time,update_time,deleted,version FROM user WHERE deleted=0 AND name = ?
    }


    @Test
    public void testSelectCount() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("age", 20, 30);

        Integer count = userMapper.selectCount(queryWrapper);
        System.out.println(count);
        //SELECT COUNT(1) FROM user WHERE deleted=0 AND age BETWEEN ? AND ?
    }


    @Test
    public void testSelectList() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        map.put("name", "Jack");
        map.put("age", 20);
        queryWrapper.allEq(map);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
        /**
         * SELECT id,name,age,email,create_time,update_time,deleted,version
         * FROM user WHERE deleted=0 AND name = ? AND id = ? AND age = ?
         */
    }


    @Test
    public void testSelectMaps() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .notLike("name", "e")
                .likeRight("email", "t");

        List<Map<String, Object>> maps = userMapper.selectMaps(queryWrapper);//返回值是Map列表
        maps.forEach(System.out::println);
        // SELECT id,name,age,email,create_time,update_time,deleted,version
        //FROM user WHERE deleted=0 AND name NOT LIKE ? AND email LIKE ?
    }

    @Test
    public void testSelectObjs() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //queryWrapper.in("id", 1, 2, 3);
        queryWrapper.inSql("id", "select id from user where id < 3");

        List<Object> objects = userMapper.selectObjs(queryWrapper);//返回值是Object列表
        objects.forEach(System.out::println);
    }


    @Test
    public void testUpdate1() {
        //修改值
        User user = new User();
        user.setAge(99);
        user.setName("Andy");

        //修改条件
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper
                .like("name", "h")
                .or()
                .between("age", 20, 30);

        int result = userMapper.update(user, userUpdateWrapper);

        System.out.println(result);

        /**
         * UPDATE
         *         user
         *     SET
         *         name='Andy',
         *         age=99,
         *         update_time='2019-11-19 20:07:30.482'
         *     WHERE
         *         deleted=0
         *         AND name LIKE '%h%'
         *         OR age BETWEEN 20 AND 30
         */
    }

    @Test
    public void testUpdate2(){
        // 修该值
        User user = new User();
        user.setAge(99);
        user.setName("Andy");

        // 修改条件
        UpdateWrapper<User> userUpdateMapper = new UpdateWrapper<>();

        userUpdateMapper.
                like("name","h")
                .or(i-> i.eq("name","李白").ne("age",20));

        int result = userMapper.update(user, userUpdateMapper);
        System.out.println(result);
        /**
         * UPDATE
         *         user
         *     SET
         *         name='Andy',
         *         age=99,
         *         update_time='2019-11-19 20:12:57.73'
         *     WHERE
         *         deleted=0
         *         AND name LIKE '%h%'
         *         OR (
         *             name = '李白'
         *             AND age <> 20
         *         )
         */
    }

    @Test
    public void testSelectListOrderBy() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
        /**
         * SELECT
         *         id,
         *         name,
         *         age,
         *         email,
         *         create_time,
         *         update_time,
         *         view_count,
         *         version,
         *         deleted
         *     FROM
         *         user
         *     WHERE
         *         deleted=0
         *     ORDER BY
         *         id DESC
         */
    }

    @Test
    public void testSelectListLast() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit 1,2");

        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
        /**
         * SELECT id,name,age,email,create_time,update_time,deleted,version
         * FROM user WHERE deleted=0 limit 1
         */
    }

    @Test
    public void testSelectListColumn() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "name", "age");

        //可以使用vo对象代替User对象，使返回结果更准确，占用更少的数据流量
        //List<User> users = userMapper.selectList(queryWrapper);
        //users.forEach(System.out::println);

        //也可以返回map集合对象
        List<Map<String, Object>> maps = userMapper.selectMaps(queryWrapper);//返回值是Map列表
        maps.forEach(System.out::println);
        /**
         * SELECT
         *         id,
         *         name,
         *         age
         *     FROM
         *         user
         *     WHERE
         *         deleted=0
         */
    }

    @Test
    public void testUpdateSet() {

        //修改值
        User user = new User();
        user.setAge(99);

        //修改条件
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper
                .like("name", "A")
                .set("name", "老李头")//除了可以查询还可以使用set设置修改的字段
                .setSql(" email = '123@qq.com'");//可以有子查询

        int result = userMapper.update(user, userUpdateWrapper);
        /**
         * UPDATE
         *         user
         *     SET
         *         age=99,
         *         update_time='2019-11-19 20:19:46.274',
         *         name='老李头',
         *         email = '123@qq.com'
         *     WHERE
         *         deleted=0
         *         AND name LIKE '%A%'
         */
    }
}
