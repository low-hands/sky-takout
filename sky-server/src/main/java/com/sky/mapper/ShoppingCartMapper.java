package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id改商品数量
     * @param cart
     */
    @Update("update sky_take_out.shopping_cart set shopping_cart.number = #{number} where id = #{id}")
    void update(ShoppingCart cart);

    @Insert("insert into sky_take_out.shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "values(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Delete("delete from sky_take_out.shopping_cart where user_id = #{id}")
    void deleteByUserId(Long id);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}
