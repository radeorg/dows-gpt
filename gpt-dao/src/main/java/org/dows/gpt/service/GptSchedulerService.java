//package org.dows.gpt.service;
//
//import org.dows.gpt.entity.GptScheduler;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//
///**
// * 统计调度(GptScheduler)表服务接口
// *
// * @author lait.zhang@gmail.com
// * @since 2025-08-10 17:39:03
// */
//public interface GptSchedulerService {
//
//    /**
//     * 通过ID查询单条数据
//     *
//     * @param countSchedulerId 主键
//     * @return 实例对象
//     */
//    GptScheduler queryById(Long countSchedulerId);
//
//    /**
//     * 分页查询
//     *
//     * @param gptScheduler 筛选条件
//     * @param pageRequest  分页对象
//     * @return 查询结果
//     */
//    Page<GptScheduler> queryByPage(GptScheduler gptScheduler, PageRequest pageRequest);
//
//    /**
//     * 新增数据
//     *
//     * @param gptScheduler 实例对象
//     * @return 实例对象
//     */
//    GptScheduler insert(GptScheduler gptScheduler);
//
//    /**
//     * 修改数据
//     *
//     * @param gptScheduler 实例对象
//     * @return 实例对象
//     */
//    GptScheduler update(GptScheduler gptScheduler);
//
//    /**
//     * 通过主键删除数据
//     *
//     * @param countSchedulerId 主键
//     * @return 是否成功
//     */
//    boolean deleteById(Long countSchedulerId);
//
//}
