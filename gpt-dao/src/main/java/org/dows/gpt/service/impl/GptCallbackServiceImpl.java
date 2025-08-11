//package org.dows.gpt.service.impl;
//
//import org.dows.gpt.entity.GptCallbackEntity;
//import org.dows.gpt.dao.GptCallbackDao;
//import org.dows.gpt.service.GptCallbackService;
//import org.springframework.stereotype.Service;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//
//import javax.annotation.Resource;
//
///**
// * 回调(GptCallback)表服务实现类
// *
// * @author lait.zhang@gmail.com
// * @since 2025-08-10 17:39:02
// */
//@Service("gptCallbackService")
//public class GptCallbackServiceImpl implements GptCallbackService {
//    @Resource
//    private GptCallbackDao gptCallbackDao;
//
//    /**
//     * 通过ID查询单条数据
//     *
//     * @param gptCallbackId 主键
//     * @return 实例对象
//     */
//    @Override
//    public GptCallbackEntity queryById(Long gptCallbackId) {
//        return this.gptCallbackDao.queryById(gptCallbackId);
//    }
//
//    /**
//     * 分页查询
//     *
//     * @param gptCallbackEntity 筛选条件
//     * @param pageRequest 分页对象
//     * @return 查询结果
//     */
//    @Override
//    public Page<GptCallbackEntity> queryByPage(GptCallbackEntity gptCallbackEntity, PageRequest pageRequest) {
//        long total = this.gptCallbackDao.count(gptCallbackEntity);
//        return new PageImpl<>(this.gptCallbackDao.queryAllByLimit(gptCallbackEntity, pageRequest), pageRequest, total);
//    }
//
//    /**
//     * 新增数据
//     *
//     * @param gptCallbackEntity 实例对象
//     * @return 实例对象
//     */
//    @Override
//    public GptCallbackEntity insert(GptCallbackEntity gptCallbackEntity) {
//        this.gptCallbackDao.insert(gptCallbackEntity);
//        return gptCallbackEntity;
//    }
//
//    /**
//     * 修改数据
//     *
//     * @param gptCallbackEntity 实例对象
//     * @return 实例对象
//     */
//    @Override
//    public GptCallbackEntity update(GptCallbackEntity gptCallbackEntity) {
//        this.gptCallbackDao.update(gptCallbackEntity);
//        return this.queryById(gptCallbackEntity.getGptCallbackId());
//    }
//
//    /**
//     * 通过主键删除数据
//     *
//     * @param gptCallbackId 主键
//     * @return 是否成功
//     */
//    @Override
//    public boolean deleteById(Long gptCallbackId) {
//        return this.gptCallbackDao.deleteById(gptCallbackId) > 0;
//    }
//}
