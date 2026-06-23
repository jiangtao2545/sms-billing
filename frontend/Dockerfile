FROM devopshub.gt.cn/devops/openresty:1.27.1.1-arm64-v1
COPY nginx-dev.conf /usr/local/openresty/nginx/conf/nginx-dev.conf
COPY nginx-prod.conf /usr/local/openresty/nginx/conf/nginx-prod.conf
COPY /frontend/dist /usr/share/nginx/html
CMD "/usr/local/openresty/bin/openresty" "-s stop"
CMD "/usr/local/openresty/bin/openresty" "-c" "/usr/local/openresty/nginx/conf/nginx-$PROFILE.conf" "-g" "daemon off;"
