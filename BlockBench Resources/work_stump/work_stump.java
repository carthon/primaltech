Stream.of(
Block.makeCuboidShape(11, 6, 5, 13, 10, 13),
Block.makeCuboidShape(4, -2, 4, 12, 5, 7),
Block.makeCuboidShape(4, -2, 9, 12, 4, 12),
Block.makeCuboidShape(9, -2, 4, 12, 5, 12),
Block.makeCuboidShape(4, -2, 4, 7, 5, 12),
Block.makeCuboidShape(3, 0, 3, 13, 6, 13),
Block.makeCuboidShape(1, 11, 1, 15, 16, 2),
Block.makeCuboidShape(2, 10, 2, 14, 15, 14),
Block.makeCuboidShape(1, 11, 14, 15, 16, 15),
Block.makeCuboidShape(14, 11, 2, 15, 16, 14),
Block.makeCuboidShape(1, 11, 2, 2, 16, 14),
Block.makeCuboidShape(3, 6, 3, 13, 10, 5),
Block.makeCuboidShape(3, 6, 5, 5, 10, 13)
).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);});