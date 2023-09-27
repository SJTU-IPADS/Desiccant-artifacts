CUR_DIR=`pwd`
cd ../lambda/lambda-functions/desiccant-image/java/aws-lambda-base-images
cat c7b2c13b366af485737f5b69b15a45b4c4d5f7a2c13856523e3d227c63a462a4-split* > c7b2c13b366af485737f5b69b15a45b4c4d5f7a2c13856523e3d227c63a462a4.tar.xz
cd $CUR_DIR
cd ../exp/fig9,10/azurefunctions-dataset2019/
cat invocations_per_function_md.anon.d01.csv-split* > invocations_per_function_md.anon.d01.csv
