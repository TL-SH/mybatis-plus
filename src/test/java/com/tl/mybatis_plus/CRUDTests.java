package com.tl.mybatis_plus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tl.mybatis_plus.entity.User;
import com.tl.mybatis_plus.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tanglei
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CRUDTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert() {
        User user = new User();

        user.setName("古力娜扎");
        user.setAge(18);
        user.setEmail("123@qq.com");

        int result = userMapper.insert(user);
        log.info("影响的行数:"+result);
    }


    @Test
    public void testUpdateById(){
        User user = new User();
        user.setId(1196742663043301378l);
        user.setAge(28);
        int result = userMapper.updateById(user);
        log.info("影响的行数:"+result);

    }

    /**
     * 并发访问中的数据不一致的解决方案:
     *  1.加锁:悲观锁,适合并发量不太大,但是写多的情况
     *  2.引入缓存: redis 单线程 多路io复用 定时做数据同步,适合高并发
     *  3.乐观锁:对数据库增加了一个字段version
     *      读数据是读取version=1
     *      修改数据时版本号+1 where version = 1
     *      适合场景,适合并发量不太大的,写少读多的情况
     *      保证了并发的一致性
     */
    @Test
    /*synchronized*/ public void testConcurrentUpdate(){
        // 查询数据
        User user1 = userMapper.selectById(1l);
        // 修改数据
        user1.setViewCount(user1.getViewCount()+1);

        // 另一个用户操作
        User user2 = userMapper.selectById(1l);
        user2.setViewCount(user2.getViewCount()+1);
        int result2 = userMapper.updateById(user2);
        log.info(result2>0 ? "user2更新成功":"user2更新失败" );

        // 执行更新
        int result1 = userMapper.updateById(user1);
        log.info(result1>0 ? "user1更新成功":"user1更新失败" );
    }


    @Test
    public void testOptimisticLocker(){
        // 查询数据
        User user1 = userMapper.selectById(1l);
        // 修改数据
        user1.setViewCount(user1.getViewCount()+1);
        // 执行更新
        int result1 = userMapper.updateById(user1);
        log.info(result1>0 ? "user1更新成功":"user1更新失败" );
    }

    @Test
    public void testSelectBatchIds(){
        List<User> list = userMapper.selectBatchIds(Arrays.asList(1, 2, 3));
        list.forEach(System.out::println);
    }

    @Test
    public void testSelectByMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("name","leishuai");
        map.put("age",18);
        List<User> list = userMapper.selectByMap(map);
        list.forEach(System.out::println);
    }


    @Test
    public void testSelectPage() {
        Page<User> page = new Page<>(1,5);
        userMapper.selectPage(page,null);
        System.out.println(page.getRecords());
        System.out.println(page.getSize());
        System.out.println(page.getTotal());
        System.out.println(page.hasNext());
        System.out.println(page.hasPrevious());
    }

    /**
     * 当指定了特定的查询列时，希望分页结果列表只返回被查询的列，而不是很多null值
     * 可以使用selectMapsPage返回Map集合列表
     */
    @Test
    public void testSelectPageMap(){
        Page<User> page = new Page<>(1,5);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name","age");

        IPage<Map<String, Object>> mapIPage = userMapper.selectMapsPage(page,queryWrapper );
        //注意：此行必须使用 mapIPage 获取记录列表，否则会有数据类型转换错误
        List<Map<String, Object>> records = mapIPage.getRecords();
        records.forEach(System.out::println);
    }

    @Test
    public void testDeleteById(){
        int result = userMapper.deleteById(100L);
        System.out.println(result);
    }


    @Test
    public void testDeleteBatchIds() {
        int result = userMapper.deleteBatchIds(Arrays.asList(8, 9, 10));
        System.out.println(result);
    }


    @Test
    public void testDeleteByMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("name","古力娜扎");
        int result = userMapper.deleteByMap(map);
        log.info("影响了几行:"+result);
    }


    /**
     * 测试 逻辑删除后的查询：
     * 不包括被逻辑删除的记录
     */
    @Test
    public void testLogicDeleteSelect() {
        User user = new User();
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }












}