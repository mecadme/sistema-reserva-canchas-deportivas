FROM node:20-alpine AS build
RUN corepack enable && corepack prepare pnpm@10 --activate
WORKDIR /workspace
COPY pnpm-workspace.yaml package.json pnpm-lock.yaml ./
COPY shell/package.json shell/package.json
COPY mf-clientes/package.json mf-clientes/package.json
COPY mf-administracion/package.json mf-administracion/package.json
COPY mf-reportes/package.json mf-reportes/package.json
RUN pnpm install --frozen-lockfile
COPY shell ./shell
RUN pnpm --filter shell run build

FROM nginx:alpine
COPY --from=build /workspace/shell/dist/shell /usr/share/nginx/html
COPY shell/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
