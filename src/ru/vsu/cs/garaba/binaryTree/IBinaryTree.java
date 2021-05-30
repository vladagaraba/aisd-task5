package ru.vsu.cs.garaba.binaryTree;

import java.awt.*;

public interface IBinaryTree<Integer>
{
    interface TreeNode<Integer>
    {
        Integer getValue();

        default TreeNode<Integer> getLeft()
        {
            return null;
        }

        default TreeNode<Integer> getRight()
        {
            return null;
        }

        default Color getColor() {
            return Color.BLACK;
        }
    }

    TreeNode<Integer> getRoot();
}