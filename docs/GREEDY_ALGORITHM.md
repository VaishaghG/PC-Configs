# Phase 4: Greedy Algorithm — PC Build Assistant

## 1. Overview
As part of **Phase 4 of the Data Structures and Algorithms (DSA) Mini-Project**, the **Greedy PC Build Assistant** enables users to specify a maximum budget in INR (₹) and automatically generates a complete 5-slot PC build (CPU, GPU, RAM, Storage, Cabinet) that minimizes component costs while strictly satisfying the budget constraint.

---

## 2. Problem Formulation
Given:
- A finite universe of hardware inventory $P = \{p_1, p_2, \dots, p_N\}$ partitioned into 5 disjoint mandatory categories:
  $$C = \{\text{CPU}, \text{GPU}, \text{RAM}, \text{STORAGE}, \text{CABINET}\}$$
- An eligibility filter for candidate products:
  $$\text{Eligible}(p) \iff p \in P \land p.\text{stockQuantity} > 0 \land p.\text{price} > 0$$
- A user budget constraint $B \in \mathbb{R}^+$.

**Objective:**  
Select exactly one eligible product $s_k \in C_k$ for each category $k \in C$ such that:
$$\sum_{k \in C} s_k.\text{price} \le B$$
and the total cost is minimized.

---

## 3. Greedy Strategy & Selection Rule

### Selection Rule:
For each category $k \in C$:
1. **Filtering:** Identify all eligible candidates:
   $$E_k = \{p \in P \mid p.\text{category} = k \land \text{Eligible}(p)\}$$
   If $E_k = \emptyset$, the build is infeasible due to missing inventory in slot $k$.
2. **Local Optimal Choice:** Sort $E_k$ in ascending order of price using the custom $O(M \log M)$ `ProductMergeSort` algorithm:
   $$s_k = \arg\min_{p \in E_k} p.\text{price}$$
3. **Global Feasibility Check:**
   $$\sum_{k \in C} s_k.\text{price} \le B$$
   If the minimum sum across all 5 slots exceeds $B$, no valid complete build can be generated. The system returns an explicit error reporting the minimum required cost and the deficit amount.
4. **Stepwise Budget Commitment:**
   As each slot $k$ is committed:
   $$\text{remainingBudget} \leftarrow \text{remainingBudget} - s_k.\text{price}$$
   Ensuring:
   $$s_k.\text{price} \le \text{remainingBudget}$$

---

## 4. Complexity Analysis

| Metric | Complexity | Notes |
|---|---|---|
| **Time Complexity** | $O(N \log N)$ | Partitioning catalog into 5 categories takes $O(N)$ time. Sorting candidates per category using `ProductMergeSort` takes $\sum_{k=1}^5 O(M_k \log M_k) = O(N \log N)$. Iterating and selecting takes $O(1)$. Total time is $O(N \log N)$. |
| **Auxiliary Space** | $O(N)$ | Temporary arrays created during MergeSort divide-and-conquer steps and selected component DTO buffers. |

---

## 5. Limitations
1. **Minimum-Cost Focus:** The greedy selection rule prioritizes the lowest-priced component per slot to maximize the likelihood of staying within tight budgets. It does not perform multi-objective Pareto optimization (e.g., maximizing benchmark gaming score per rupee).
2. **Discrete Fixed Cardinality:** The algorithm assumes exactly 1 product per required slot (5 items total). It does not solve the unbounded 0/1 knapsack problem for variable-sized accessories.

---

## 6. Implementation Architecture

- **Core Algorithm & Service:** [`GreedyBuildService.java`](file:///d:/DS/PC%20Config/backend/src/main/java/com/pcconfigurations/backend/service/GreedyBuildService.java)
- **Data Transfer Objects:**
  - Request: [`GreedyBuildRequest.java`](file:///d:/DS/PC%20Config/backend/src/main/java/com/pcconfigurations/backend/dto/GreedyBuildRequest.java) (`budget`)
  - Response: [`GreedyBuildResponse.java`](file:///d:/DS/PC%20Config/backend/src/main/java/com/pcconfigurations/backend/dto/GreedyBuildResponse.java) (`budget`, `totalPrice`, `remainingBudget`, `selectedComponents`, `strategy`, `message`)
- **REST Endpoints:**
  - `POST /api/builds/greedy` (in [`PcBuildController.java`](file:///d:/DS/PC%20Config/backend/src/main/java/com/pcconfigurations/backend/controller/PcBuildController.java))
  - `GET /api/algorithms/demo` (in [`AlgorithmController.java`](file:///d:/DS/PC%20Config/backend/src/main/java/com/pcconfigurations/backend/controller/AlgorithmController.java))
- **Frontend UI:**
  - Topbar action **"Build Within Budget"** & rail trigger in [`builder.html`](file:///d:/DS/PC%20Config/frontend/pages/builder.html)
  - Interactive recommendation modal with presets, review card, and one-click application to the custom rig
  - Telemetry card on the User Dashboard [`dashboard.html`](file:///d:/DS/PC%20Config/frontend/pages/dashboard.html)
- **Automated Test Suite:**
  - Unit Tests: [`GreedyBuildTest.java`](file:///d:/DS/PC%20Config/backend/src/test/java/com/pcconfigurations/backend/GreedyBuildTest.java) (7 test cases)
  - API Test Suite: [`test_greedy_api.ps1`](file:///d:/DS/PC%20Config/test_greedy_api.ps1) (5 end-to-end scenarios)
