FROM openjdk:17-slim

# Thư mục chứa mã người dùng
WORKDIR /code

# Copy script vào container
COPY run.sh /run.sh
RUN chmod +x /run.sh

# Mặc định chạy script
ENTRYPOINT ["/run.sh"]
