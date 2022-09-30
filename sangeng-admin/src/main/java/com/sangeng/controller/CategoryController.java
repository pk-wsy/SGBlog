package com.sangeng.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.ExcelCategoryVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.service.CategoryService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/content/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    /**
     * 实现展示出category表中所有的分类（返回name、id、描述作为data即可）
     * 用于在写博文栏目下点击分类会弹出所有分类的下拉框
     * 实现写博文功能的第一个接口
     * @return
     */
    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory(){
        return categoryService.listAllCategory();
    }


    /**
     * 用于实现将后台页面上的分类数据信息，导出到excel文件中
     * 这里返回值是void是因为导出excel格式的文件已经设置相应类型是xlsx对应的类型，不能再传输json了，一个响应只能传输一种响应内容
     * @param response
     */
    //对于该接口，需要进行权限的判断，要不然携带token的后台用户如果没有操作此功能的权限，也可以通过访问后端接口完成此功能
    //在这里使用自定义权限校验方法
    @PreAuthorize("@ps.hasPermission('content:category:export')")
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        try {
            //设置下载excel文件请求头中的相关信息
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            //获取我们需要导出到excel文件中的数据
            List<Category> categoryList = categoryService.list();
            //拷贝至Vo对象
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryList, ExcelCategoryVo.class);
            //把数据写入到excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(excelCategoryVos);

        } catch (Exception e) {
            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }
}
