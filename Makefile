.PHONY: help build up down restart logs ps clean test

help:
	@echo "Budget Management System - Docker Commands"
	@echo ""
	@echo "  make build    - Build all Docker images"
	@echo "  make up       - Start all services"
	@echo "  make down     - Stop all services"
	@echo "  make restart  - Restart all services"
	@echo "  make logs     - Show logs"
	@echo "  make ps       - Show running containers"
	@echo "  make clean    - Stop and remove all"
	@echo "  make test     - Test all services health"

build:
	@echo "===Building Docker images==="
	docker-compose build

up:
	@echo "===Starting services==="
	docker-compose up -d
	@echo "===Waiting for services==="
	@sleep 15
	@make ps

down:
	@echo "===Stopping services==="
	docker-compose down

restart:
	@echo "===Restarting services==="
	docker-compose restart

logs:
ifdef SERVICE
	docker-compose logs -f $(SERVICE)
else
	docker-compose logs -f
endif

ps:
	@echo "===Running containers==="
	@docker-compose ps

clean:
	@echo "===Cleaning up==="
	docker-compose down -v
	docker system prune -f

test:
	@echo "===Testing services==="
	@echo "User Service (8080):"
	@curl -s http://localhost:8080/q/health | jq . || echo "X"
	@echo ""
	@echo "Budget Service (8081):"
	@curl -s http://localhost:8081/q/health | jq . || echo "X"
	@echo ""
	@echo "Transaction Service (8082):"
	@curl -s http://localhost:8082/q/health | jq . || echo "X"
