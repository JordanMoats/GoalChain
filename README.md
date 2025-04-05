# Goal Chain

Stop juggling complex OSRS goal dependencies in your head! Goal Chain is a RuneLite task tracker designed for how RuneScape *actually* works, where quests need skills, bosses need gear, and tasks unlock further tasks.

Simply add your goals and define their prerequisites. Goal Chain's power lies in the **Active Tasks** list: it automatically shows *only* the goals you can currently work on because you've completed the necessary steps.

Focus on progressing, not on figuring out *what* to progress next.

**Features:**
*   Create tasks quickly.
*   Easily define prerequisite tasks for any goal.
*   Instantly add follow-up tasks ("Next Steps") linked to prerequisites.
*   Automatic **Active Tasks** list shows only actionable goals.
*   Clear views for Pending (blocked) and Completed tasks.
*   Simple checklist interface with inline actions for managing tasks.


# Todo Chain Dev Plan

## UI
- Quick Add
  - A text box for adding a goal with no prerequisites, instantly shows up in active list
- Active List
  - Alphabetical list or manually ordered?
    - Go with alphabetical for now for ease, custom orders later
    - Probs manually ordered, but then how manage the order? click and drag?
    - How interact with focused goals.
  - List of goal that have no incomplete prereqs
  - Each line is a checklist item with a checkbox to complete the goal and the goal text
  - There's buttons to add a prereq or subsequent goal. Right click menu too.
    - Clicking one of these opens a quick text box to name the new goal.
- Inactive List
  - List of goals that have incomplete prereqs. You can add and remove sub and prereq goals.
- Collapsible Completed List
  - todo Can you uncomplete goals? What happens if you uncheck a goal that another goal depended on?
    - maybe just make it so you can't uncheck goals that are prereqs for other already-completed goals

* IDEA
  * A way to select a primary or focus goal that would add a star to that goal and all prereqs and their prereqs and so on.