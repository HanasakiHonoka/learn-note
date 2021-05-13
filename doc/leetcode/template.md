# leetcode 刷题用代码模板

## 数据结构

* 链表节点 ListNode

```java
public class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
        this.next = null;
    }

}
```

* 链表 LinkList

```java
public class LinkList {

    public static void insert(ListNode head, int val) {
        ListNode p = head.next;
        while (p != null) {
            p = p.next;
            head = head.next;
        }
        p = new ListNode(val);
        head.next = p;
    }

    public static void insert(ListNode head, ListNode val) {
        insert(head, val.val);
    }

    public static void print(ListNode head) {
        ListNode p = head.next;
        boolean isFirst = true;
        while (p != null) {
            if (isFirst) {
                System.out.print(p.val);
                isFirst = false;
            } else {
                System.out.print("->" + p.val);
            }
            p = p.next;
        }
        System.out.println();
    }

    public static void printWithoutHead(ListNode head) {
        ListNode newHead = new ListNode(0);
        newHead.next = head;
        print(newHead);
    }

    public static ListNode create(int []array) {
        ListNode head = new ListNode(0);
        for (int i = 0; i < array.length; i++) {
            insert(head, array[i]);
        }
        return head;
    }
}
```

* 二叉树

```java
public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;

    public TreeNode(int x) {
        val = x;
    }

    public static void pre(TreeNode root) {
        if (root == null) return;
        System.out.print(root.val + " ");
        pre(root.left);
        pre(root.right);
    }

    public static void in(TreeNode root) {
        if (root == null) return;
        in(root.left);
        System.out.print(root.val + " ");
        in(root.right);
    }

    public static TreeNode create(int [] pre, int [] in) {
        if (pre.length == 0 || in.length == 0) return null;
        TreeNode root = new TreeNode(pre[0]);
        int indexin = 0;
        for (int i = 0; i < in.length; i++) {
            if (in[i] == pre[0]) {
                indexin = i;
                break;
            }
        }
        int leftLen = indexin;
        int rightLen = pre.length - indexin - 1;
        int[] preLeft = Arrays.copyOfRange(pre, 1, 1 + leftLen);
        int[] preRight = Arrays.copyOfRange(pre, 1 + leftLen, pre.length);

        int[] inLeft = Arrays.copyOfRange(in, 0, indexin);
        int[] inRight = Arrays.copyOfRange(in, indexin + 1, in.length);

        root.left = create(preLeft, inLeft);
        root.right = create(preRight, inRight);
        return root;
    }

    public static TreeNode create(String data) {
        if (data == "") return null;
        String[] val = data.replace(" ", "").replace("[", "").replace("]", "").split(",");
        Queue<TreeNode> q = new LinkedList<>();
        TreeNode root = new TreeNode(Integer.parseInt(val[0]));
        q.add(root);
        int index = 1;
        while (!q.isEmpty() && index < val.length) {
            TreeNode poll = q.poll();
            if (!val[index].equals("null")) {
                poll.left = new TreeNode(Integer.parseInt(val[index]));
                q.add(poll.left);
            }
            index++;
            if (index < val.length && !val[index].equals("null")) {
                poll.right = new TreeNode(Integer.parseInt(val[index]));
                q.add(poll.right);
            }
            index++;
        }
        return root;
    }
}
```

## 测试代码生成器

```java
public class TestDataGen {
    public static void main(String[] args) {
        String[] optList = {"RandomizedCollection","insert","insert","insert","insert","insert","insert","remove","remove","remove","remove","getRandom","getRandom","getRandom","getRandom","getRandom","getRandom","getRandom","getRandom","getRandom","getRandom"};
        Object[][] paramList = {{},{1},{1},{2},{1},{2},{2},{1},{2},{2},{2},{},{},{},{},{},{},{},{},{},{}};
        String main = "problemxxx";

        boolean isPrint = true;
        int len = optList.length;
        for (int i = 1; i < len; i++) {
            String opt = optList[i];
            Object[] params = paramList[i];
            StringBuffer sb = new StringBuffer();
            sb.append(main).append(".").append(opt).append("(");
            for (Object param : params) {
                sb.append(param + ",");
            }
            if (params.length > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(")");
            if (isPrint) {
                sb.insert(0, "System.out.println(");
                sb.append(");");
            } else {
                sb.append(";");
            }

            System.out.println(sb.toString());
        }

    }
}
```