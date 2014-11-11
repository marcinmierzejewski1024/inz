package com.example.inz.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by dom on 12.10.14.
 */
public class Category extends AbsDatabaseItem
{
    @DatabaseField(generatedId = true)
    long categoryId;
    @DatabaseField()
    long parentCategoryId;
    @DatabaseField()
    String name;
    @DatabaseField()
    String hexColor;

    Category parentCategory;

    public long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Category getParentCategory()
    {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory)
    {
        this.parentCategory = parentCategory;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        setChanged();
        this.name = name;
    }

    public long getParentCategoryId()
    {
        return parentCategoryId;
    }

    public void setParentCategoryId(long parentCategoryId)
    {
        this.parentCategoryId = parentCategoryId;
    }

    public String getHexColor()
    {
        return hexColor;
    }

    public void setHexColor(String hexColor)
    {
        this.hexColor = hexColor;
    }

    public Category(String name, Category parentCategory)
    {
        this.name = name;
        this.parentCategory = parentCategory;
    }

    public Category(String name)
    {
        this.name = name;

    }

    public Category()
    {
    }
}
