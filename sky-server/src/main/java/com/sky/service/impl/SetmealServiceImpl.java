package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO){
        // 向套餐表插入数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        // 获取生成的套餐id
        Long setmealID = setmeal.getId();
        log.info("套餐id:{}",setmealID);
        // 找到对应的菜品List
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 给菜品设置套餐id
        setmealDishes.forEach(setmealDish ->{
            setmealDish.setSetmealId(setmealID);
        });
        // 向套餐菜品表插入数据
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id->{
            Setmeal setmeal = setmealMapper.getbyId(id);
            // 如果售卖中，则不能删除
            if (setmeal.getStatus()==StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        ids.forEach(setmealId ->{
            setmealMapper.deleteBYId(setmealId);
            setmealDishMapper.deleteBySetmealID(setmealId);
        });
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.getbyId(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        // 修改套餐
        setmealMapper.update(setmeal);
        // 得到套餐id
        Long setmealId = setmeal.getId();
        // 删除套餐和菜品的对应关系
        setmealDishMapper.deleteBySetmealID(setmealId);
        // 找到对应菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 对应新的菜品到套餐中
        setmealDishes.forEach(setmealDish ->{
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void StartOrStop(Integer status, Long id) {
        // 有停售菜品，则不好启用
        if(status==StatusConstant.ENABLE){
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            dishList.forEach(dish->{
                if(dish.getStatus()==StatusConstant.DISABLE){
                    throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                }
            });
        }
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
