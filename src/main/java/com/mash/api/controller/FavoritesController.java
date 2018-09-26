package com.mash.api.controller;

import com.mash.api.entity.Favorites;
import com.mash.api.entity.Position;
import com.mash.api.entity.Result;
import com.mash.api.service.FavoritesService;
import com.mash.api.service.PositionService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class FavoritesController {

    private static final Logger log = LoggerFactory.getLogger(FavoritesController.class);

    @Autowired
    private FavoritesService favoritesService;

    @Autowired
    private PositionService positionService;

    /**
     * 加入收藏
     * @param positionId
     * @param request
     * @return
     */
    @PostMapping(value="/favorites")
    public Result save(@RequestParam("positionId")Integer positionId,
                       HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        Favorites favorites = new Favorites();
        favorites.setAccountId(accountId);
        Position position = positionService.findById(positionId);
        favorites.setPosition(position);

        favorites = favoritesService.save(favorites);
        log.info("用户：" + accountId + " 将站点：" + positionId + " 加入收藏");
        return ResultUtil.success();
    }

    /**
     * 清空收藏夹
     * @return
     */
    @DeleteMapping(value="/favorites/remove")
    public Result removeAll(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        favoritesService.removeAll(accountId);
        return ResultUtil.success();
    }

    /**
     *  移除收藏
     * @param positionId
     * @param request
     * @return
     */
    @DeleteMapping(value="/favorites/{positionId}")
    public Result delete(@PathVariable("positionId")Integer positionId,
                         HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        Favorites favorites = favoritesService.findByAccountIdAndPositionId(accountId, positionId);
        if (favorites != null)
        {
            favoritesService.remove(favorites);
        }
        log.info("用户：" + accountId + " 将站点：" + positionId + " 移除收藏");
        return ResultUtil.success();
    }

    /**
     * 查询用户所有收藏
     * @return
     */
    @GetMapping(value="/favorites")
    public Result findAll(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        List<Favorites> favoritesList = favoritesService.findByAccountId(accountId);
        return ResultUtil.success(favoritesList);
    }

    /**
     *
     * @param positionId
     * @param request
     * @return
     */
    @GetMapping(value="/favorites/{positionId}")
    public Result findOne(@PathVariable("positionId")Integer positionId,
                          HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        Favorites favorites = favoritesService.findByAccountIdAndPositionId(accountId, positionId);
        return ResultUtil.success(favorites);
    }
}
