// 等待文档加载完成
$(document).ready(function() {
    // 加载导航栏
    $('#navbar').load('/aireport/navbar.html', function() {
        // 导航栏加载完成后，初始化相关的事件

        // 获取模态框元素
        var bagsModal = document.getElementById("bagsModal");
        var bagsSpan = bagsModal.getElementsByClassName("close")[0];

        // 显示报告包的函数
        window.showBags = function() {
            // 加载用户的报告包列表
            fetch('/aireport/app/bagOperation', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'action=queryBags'
            })
                .then(response => response.json())
                .then(bags => {
                    let html = '';
                    bags.forEach(bag => {
                        html += `
                            <div class="bag-item" onclick="navigateToBag(${bag.bagId})">
                                <span>
                                    <i class="entypo-folder"></i>
                                    ${bag.bagName}
                                </span>
                                <button class="btn btn-link delete-btn" onclick="event.stopPropagation(); deleteBag(${bag.bagId})">
                                    <i class="entypo-trash" style="color: #ff4444;"></i>
                                </button>
                            </div>
                        `;
                    });
                    document.getElementById('bagsList').innerHTML = html;
                    bagsModal.style.display = "block";
                });
        }

        // 导航到特定报告包的函数
        window.navigateToBag = function(bagId) {
            window.location.href = `/aireport/app/bagDocumentListView?bagId=${bagId}`;
        }

        // 关闭模态框
        bagsSpan.onclick = function() {
            bagsModal.style.display = "none";
        }

        // 点击模态框外部关闭
        window.onclick = function(event) {
            if (event.target == bagsModal) {
                bagsModal.style.display = "none";
            }
        }

        // 创建新报告包的函数
        window.createNewBag = function() {
            var bagName = document.getElementById('newBagName').value;
            
            if (!bagName.trim()) {
                alert('请输入报告包名称');
                return;
            }

            fetch('/aireport/app/bagOperation', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'action=createBag&bagName=' + encodeURIComponent(bagName)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 清空输入框
                    document.getElementById('newBagName').value = '';
                    // 重新加载报告包列表
                    showBags();
                } else {
                    alert('创建失败：' + data.message);
                }
            })
            .catch(error => {
                alert('创建出错：' + error);
            });
        }

        // 添加回车键提交功能
        $(document).on('keypress', '#newBagName', function(e) {
            if (e.key === 'Enter') {
                window.createNewBag();
            }
        });

        // 删除报告包的函数
        window.deleteBag = function(bagId) {
            if (confirm('确定要删除这个报告包吗？')) {
                fetch('/aireport/app/bagOperation', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `action=deleteBag&bagId=${bagId}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showBags(); // 刷新列表
                    } else {
                        alert('删除失败：' + data.message);
                    }
                })
                .catch(error => {
                    alert('删除出错：' + error);
                });
            }
        }
    });
});