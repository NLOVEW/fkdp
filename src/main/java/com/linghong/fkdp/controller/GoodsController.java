package com.linghong.fkdp.controller;

import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.pojo.Goods;
import com.linghong.fkdp.service.GoodsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/28 14:37
 * @Version 1.0
 * @Description:
 */
@RestController
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    /**
     * 添加商品
     * 参数 ： userId title goodsType originalPrice number(传入时需限定数量最大100)
     *          empressPrice introduce startTime  endTime downPrice
     *          base64Images(图片以中文。为分隔符)
     * @param userId
     * @param goods
     * @param base64Images
     * @return
     */
    @PostMapping("/goods/addGoods")
    public Response addGoods(Long userId,
                             Goods goods,
                             String base64Images){
        boolean flag = goodsService.addGoods(userId,goods,base64Images);
        if (flag){
            return new Response(true,200 ,null ,"添加成功" );
        }
        return new Response(false,101 , null, "商铺暂未通过认证");
    }

    /**
     * //fixme 更新商品信息  竞拍开始时间  ☆☆ 结束时间不允许修改 ☆☆
     * 参数 ： goodsId title goodsType originalPrice number empressPrice introduce
     *         startTime  endTime downPrice base64Images(图片以中文。为分隔符)
     * @param goods
     * @param base64Images
     * @return
     */
    @PostMapping("/goods/updateGoods")
    public Response updateGoods(Goods goods,
                                @RequestParam(required = false) String base64Images){
        boolean flag = goodsService.updateGoods(goods,base64Images);
        if (flag){
            return new Response(true,200 ,null ,"更新成功" );
        }
        return new Response(false,101 , null, "更新失败");
    }

    /**
     * 逻辑删除商品
     * @param goodsId
     * @return
     */
    @DeleteMapping("/goods/deleteGoods/{goodsId}")
    public Response deleteGoods(@PathVariable String goodsId){
        boolean flag = goodsService.deleteGoods(goodsId);
        if (flag){
            return new Response(true,200 ,null ,"删除成功" );
        }
        return new Response(false,101 , null, "删除失败");
    }

    /**
     * 根据商品Id 查询商品详细信息
     * @param goodsId
     * @return
     */
    @GetMapping("/goods/findGoodsByGoogsId/{goodsId}")
    public Response findGoodsByGoogsId(@PathVariable String goodsId){
        Goods goods = goodsService.findGoodsByGoogsId(goodsId);
        return new Response(true,200 ,goods ,"查询结果" );
    }

    /**
     * 根据商家用户的userId  查询其发布的商品
     * @param userId
     * @return
     */
    @GetMapping("/goods/findGoodsByUserId/{userId}")
    public Response findGoodsByUserId(@PathVariable Long userId){
        List<Goods> goods = goodsService.findGoodsByUserId(userId);
        return new Response(true,200 ,goods ,"查询结果" );
    }

    /**
     * 进入到首页前调用 左滑右滑使用 供前端根据此数据列表请求详细数据
     * @return
     */
    @GetMapping("/goods/getAllGoodsId")
    public Response getAllGoodsId(){
        List<String> goodsIds = goodsService.getAllGoodsId();
        return new Response(true,200 ,goodsIds ,"查询结果" );
    }

    /**
     *  首页竞拍首页数据
     *  倒计时：需要前端根据开始时间自己判断进行倒计时显示
     *  此页面信息需要20s请求一次
     * @return
     */
    @GetMapping("/goods/getAuctionIndex/{goodsId}")
    public Response getAuctionIndex(@PathVariable String goodsId){
        Map<String,Object> result = goodsService.getAuctionIndex(goodsId);
        return new Response(true,200 ,result ,"查询结果" );
    }

    //todo  -------------------检索商品--------------------------------------

    /**
     * 根据城市 和 商品类型进行检索
     * @param city
     * @param goodsType
     * @return
     */
    @GetMapping("/goods/findByCityAndType/{city}/{goodsType}")
    public Response findByCityAndType(@PathVariable String city,
                               @PathVariable String goodsType){
        List<Goods> goods = goodsService.findByCityAndType(city,goodsType);
        return new Response(true,200 ,goods ,"检索信息" );
    }

    /**
     * 商品类型进行检索 没有城市差别
     *
     * @param goodsType
     * @return
     */
    @GetMapping("/goods/findByType/{goodsType}")
    public Response findByType(@PathVariable String goodsType){
        List<Goods> goods = goodsService.findByType(goodsType);
        return new Response(true,200 ,goods ,"检索信息" );
    }

    /**
     * 根据城市  以及 关键词查询
     * @param city
     * @param key
     * @return
     */
    @GetMapping("/goods/findByCityAndKey/{city}/{key}")
    public Response findByCityAndKey(@PathVariable String city,
                                     @PathVariable String key){
        List<Goods> goods = goodsService.findByCityAndKey(city,key);
        return new Response(true,200 ,goods ,"检索信息" );
    }


    /**
     * 关键词查询  没有城市限制
     *
     * @param key
     * @return
     */
    @GetMapping("/goods/findByCityAndKey/{key}")
    public Response findByKey(@PathVariable String key){
        List<Goods> goods = goodsService.findByKey(key);
        return new Response(true,200 ,goods ,"检索信息" );
    }
}
